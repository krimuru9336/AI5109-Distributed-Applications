const express = require('express');
const http = require('http');
const socketIo = require('socket.io');
const validator = require('validator');
const crypto = require('crypto');
const dotenv = require('dotenv');
const db = require('./database_connection');

dotenv.config();

async function main() {
    try {
        const app = express();
        const server = http.createServer(app);
        const io = socketIo(server);

        await db.init();

        const PORT = process.env.PORT;

        const users = await db.getAllUsers();
        const hashUser = createHashUserMap(users);
        const chatGroups = await createGroupMap();

        function createHashWithSalt(...args) {
            const salt = process.env.SALT;
            const hash = crypto.createHash('sha256');
            args.forEach(str => {
                hash.update(str + salt);
            });
            return hash.digest('hex');
        }

        function isCorrectUser(userName, userUUID) {
            return (userHashExists(userName, userUUID) && 
                hashUser.get(createHashWithSalt(userName, userUUID)) === userName);
        }

        function userHashExists(userName, userUUID) {
            return hashUser.has(createHashWithSalt(userName, userUUID));
        }

        function createHashUserMap(users) {
            let map = new Map();
            users.forEach(user => {
                map.set(user.uuid, user.userName);
            });
            return map;
        }

        async function createGroupMap() {
            const map = new Map();
            try {
                const databaseGroupEntries = await db.getAllGroupEntries();
                databaseGroupEntries.forEach(entry => {
                    let userName = hashUser.get(entry.id);
                    if (!map.has(entry.groupName)) {
                        map.set(entry.groupName, {
                            users: [userName],
                            admin: "",
                        });
                    } else {
                        map.get(entry.groupName).users.push(userName);
                    }
                    if (entry.isAdmin) {
                        map.get(entry.groupName).admin = userName;
                    }
                });
            } catch (error) {
                return map;
            }
            console.log(map);
            return map;
        }

        function findUserByName(userName) {
            return users.find(user => user.userName === userName);
        }

        function findUserBySocketID(socketID) {
            return users.find(user => user.id === socketID);
        }

        function addMessageToDatabase(sender, messageObject) {
            try {
                if (!messageObject.partnerName || !sender)
                    return;

                const user = findUserByName(messageObject.partnerName);
                messageObject.partnerName = sender.userName;
                if (messageObject.timestampEdit) {
                    db.addEditMessage(user.uuid, messageObject);
                    return;
                } else if (messageObject.deleted) {
                    messageObject.messageText = "Message has been deleted.";
                    db.addDeleteMessage(user.uuid, messageObject);
                } else {
                    db.addMessage(user.uuid, messageObject);
                }
            } catch (error) {
                console.log(error);
            }
        }

        function addUserToGroupDB(groupName, userName, isAdmin) {
            try {
                console.log("Adding user " + userName +  " to group " + groupName + " with isAdmin: " + isAdmin);
                const user = findUserByName(userName);
                console.log(user);
                if (user) {
                    db.addUserToGroup(user.uuid, groupName, isAdmin);
                }
            } catch (error) {
                console.log(error);
            }
        }

        function removeUserFromGroupDB(groupName, userName) {
            try {
                console.log("Removing user " + userName +  " from group " + groupName);
                const user = findUserByName(userName);
                if (user) {
                    db.removeUserFromGroup(user.uuid, groupName);
                }
            } catch (error) {
                console.log(error);
            }
        }

        io.on('connection', (socket) => {
            socket.on('userCheck', (userName, userUUID) => {
                try {
                    let userUnavailable = false;
                    let checkUsername = true;
                    if (userUUID != undefined && validator.isUUID(userUUID)) {
                        if (isCorrectUser(userName, userUUID)) {
                            userUnavailable = false;
                            checkUsername = false;
                        }
                    }
                    if (checkUsername && findUserByName(userName)) {
                        userUnavailable = true;
                    }
                    socket.emit('userExists', { 
                        data: userUnavailable,
                        name: userName,
                        action: 'userExists' 
                    });
                } catch (error) {
                    console.log(error);
                }
            });

            // Handle new user connection
            socket.on('registerUser', (userName, userUUID) => {
                try {
                    if (userUUID != undefined && validator.isUUID(userUUID)) {
                        if (isCorrectUser(userName, userUUID)) {
                            // Do nothing
                        } else if (!userHashExists(userName, userUUID)) {
                            const hashVal = createHashWithSalt(userName, userUUID);
                            hashUser.set(hashVal, userName);
                            db.addUser(userName, hashVal);
                            db.createUserMessageTable(hashVal);
                        }

                        // Check if users array already contains the userName and the hashed user
                        const hashedUUID = createHashWithSalt(userName, userUUID);
                        const curUser = users.find(user => (
                            user.userName === userName && user.uuid === hashedUUID
                        ));

                        if (curUser) {
                            curUser.id = socket.id;
                        } else {
                            users.push({ id: socket.id, uuid: hashedUUID, userName });
                        }
                    }
                    // Send updated user list to sender
                    sendUserListToSender(socket.id);

                    // Send updated user list to all users expect sender
                    socket.broadcast.emit('userList', { 
                        data: userName,
                        action: 'connected'
                    });
                    console.log(`User ${userName} connected`);
                } catch (error) {
                    console.log(error);
                }
            });

            // Handle incoming messages
            socket.on('message', (message) => {
                try {
                    const { targetUserId, messageId, message: messageText } = message;
                    const senderUser = findUserBySocketID(socket.id);
                    const senderUserId = senderUser?.userName;

                    if (message.isGroup) {
                        sendGroupMessage(targetUserId, senderUserId, messageId, messageText, socket);
                    } else {
                        // Find the user
                        const targetUser = findUserByName(targetUserId);

                        // If the target user is found, send the message to that user
                        if (targetUser) {
                            if (targetUser.id) {
                                io.to(targetUser.id).emit('message', {
                                    data: {
                                        senderUserId,
                                        message: messageText,
                                        messageId,
                                        timestamp: Date.now(),
                                    },
                                    action: 'message',
                                });
                            } else {
                                addMessageToDatabase(senderUser, {
                                    partnerName: targetUserId,
                                    incoming: true,
                                    messageText,
                                    timestamp: Date.now(),
                                    id: messageId,
                                });
                            }
                        }
                    }
                    io.to(socket.id).emit('timestamp', {
                        data: {
                            messageId,
                            timestamp: Date.now(),
                        },
                        action: 'timestamp',
                    });
                } catch (error) {
                    console.log(error);
                }
            });

            function sendGroupMessage(chatGroup, senderUserId, messageId, messageText, socket) {
                socket.to(chatGroup).emit('message', {
                    data: {
                        senderUserId,
                        message: messageText,
                        messageId,
                        timestamp: Date.now(),
                        chatGroup,
                    },
                    action: 'message',
                });
            }

            socket.on('joinChatGroup', (chatGroup, userName) => {
                if (chatGroups.has(chatGroup) && chatGroups.get(chatGroup).users.includes(userName)) {
                    console.log(userName + " joined chatGroup " + chatGroup);
                    socket.join(chatGroup);
                }
            });

            socket.on('socketJoinGroup', (chatGroup) => {
                socket.join(chatGroup);
            });

            socket.on('socketLeaveGroup', (chatGroup) => {
                socket.leave(chatGroup);
            });

            socket.on('createChatGroup', (chatGroup, userName) => {
                if (createChatGroupIfNotExists(chatGroup, userName)) {
                    console.log(userName + " created chatGroup " + chatGroup);
                    io.to(socket.id).emit('groupCreated', {
                        data: chatGroup,
                        action: 'groupCreated',
                    });
                    addUserToGroupDB(chatGroup, userName, true);
                    socket.join(chatGroup);
                } else {
                    io.to(socket.id).emit('groupExists', {
                        data: chatGroup,
                        action: 'groupExists',
                    });
                }
            });

            socket.on('updateChatGroup', (chatGroup, userNames) => {
                const group = chatGroups.get(chatGroup);
                if (group.admin !== findUserBySocketID(socket.id).userName) {
                    io.to(socket.id).emit('unauthorizedGroup');
                    return;
                };
                userNames = JSON.parse(userNames);
                const usersToAdd = userNames.filter(user => !group.users.includes(user));
                const usersToRemove = group.users.filter(user => {
                    return user !== group.admin && !userNames.includes(user);
                });
                addUsersToChatGroup(chatGroup, usersToAdd);
                removeUsersFromChatGroup(chatGroup, usersToRemove);
                usersToAdd.forEach(userName => {
                    const user = findUserByName(userName);
                    if (user.id) {
                        io.to(user.id).emit('joinChatGroup', {
                            data: chatGroup,
                            action: 'joinChatGroup',
                        });
                    }
                });
                usersToRemove.forEach(userName => {
                    const user = findUserByName(userName);
                    if (user.id) {
                        io.to(user.id).emit('leaveChatGroup', {
                            data: chatGroup,
                            action: 'leaveChatGroup',
                        });
                    }
                });
            });

            socket.on('getGroups', (userName) => {
                console.log(userName);
                const userGroups = Array.from(chatGroups.entries()).filter(([_, group]) => group.users.includes(userName)).map(([group, _]) => group);
                console.log(userGroups);
                socket.emit('groups', {
                    data: userGroups,
                    action: 'groups',
                });
                for (const group of userGroups) {
                    socket.join(group);
                }
            });

            socket.on('usersInGroup', (chatGroup) => {
                const group = chatGroups.get(chatGroup);
                if (group) {
                    socket.emit('usersInGroup', {
                        data: group.users,
                        action: 'usersInGroup',
                    });
                }
            });

            function createChatGroupIfNotExists(chatGroup, userName) {
                if (!chatGroups.has(chatGroup)) {
                    chatGroups.set(chatGroup, {
                        users: [userName],
                        admin: userName,
                    });
                    return true;
                }
                return false;
            }

            function removeUsersFromChatGroup(chatGroup, userNames) {
                try {
                    const group = chatGroups.get(chatGroup);
                    userNames.forEach(user => {
                        removeUserFromGroupDB(chatGroup, user);
                    });
                    group.users = group.users.filter(user => !userNames.includes(user));
                } catch (error) {
                    console.log(error);
                }
            }

            function addUsersToChatGroup(chatGroup, userNames) {
                try {
                    const group = chatGroups.get(chatGroup);
                    userNames.forEach(user => {
                        if (!group.users.includes(user)) {
                            group.users.push(user);
                            addUserToGroupDB(chatGroup, user, false);
                        }
                    });
                } catch (error) {
                    console.log(error);
                }
            }

            // Handle deleting messages
            socket.on('delete', (message) => {
                try {
                    console.log(message);
                    const { targetUserId, messageId, isGroup } = message;
                    const senderUser = findUserBySocketID(socket.id);
                    const senderUserId = senderUser?.userName;

                    if (isGroup) {
                        deleteGroupMessage(targetUserId, senderUserId, messageId, socket);
                    } else {
                        // Find the user
                        const targetUser = findUserByName(targetUserId);

                        // If the target user is found, send the message to that user
                        if (targetUser) {
                            if (targetUser.id) {
                                io.to(targetUser.id).emit('delete', {
                                    data: {
                                        senderUserId,
                                        messageId,
                                    },
                                    action: 'delete',
                                });
                            } else {
                                addMessageToDatabase(senderUser, {
                                    partnerName: targetUserId,
                                    incoming: true,
                                    timestamp: Date.now(),
                                    id: messageId,
                                    deleted: true,
                                });
                            }
                        }
                    }
                } catch (error) {
                    console.log(error);
                }
            });

            function deleteGroupMessage(chatGroup, senderUserId, messageId, socket) {
                console.log("Send delete to chatGroup " + chatGroup);
                socket.to(chatGroup).emit('delete', {
                    data: {
                        senderUserId,
                        messageId,
                        chatGroup,
                    },
                    action: 'delete',
                });
            }

            // Handle editing messages
            socket.on('edit', (message) => {
                try {
                    const { targetUserId, messageId, message: messageText, editDate, isGroup } = message;
                    const senderUser = findUserBySocketID(socket.id);
                    const senderUserId = senderUser?.userName;
                    
                    if (isGroup) { 
                        editGroupMessage(targetUserId, senderUserId, messageId, messageText, socket);
                    } else {
                        // Find the user
                        const targetUser = findUserByName(targetUserId);

                        // If the target user is found, send the message to that user
                        if (targetUser) {
                            if (targetUser.id) {
                                io.to(targetUser.id).emit('edit', {
                                    data: {
                                        senderUserId,
                                        messageId,
                                        message: messageText,
                                        editDate: Date.now(),
                                    },
                                    action: 'edit',
                                });
                            } else {
                                addMessageToDatabase(senderUser, {
                                    partnerName: targetUserId,
                                    incoming: true,
                                    messageText,
                                    timestamp: Date.now(),
                                    id: messageId,
                                    timestampEdit: Date.now()
                                });
                            }
                        }
                    }
                    io.to(socket.id).emit('timestamp', {
                        data: {
                            messageId,
                            timestamp: Date.now(),
                        },
                        action: 'editTimestamp',
                    });
                } catch (error) {
                    console.log(error);
                }
            });

            function editGroupMessage(chatGroup, senderUserId, messageId, messageText, socket) {
                console.log("Send edit to chatGroup " + chatGroup);
                socket.to(chatGroup).emit('edit', {
                    data: {
                        senderUserId,
                        messageId,
                        message: messageText,
                        editDate: Date.now(),
                        chatGroup
                    },
                    action: 'edit',
                });
            }

            // Handle user disconnection
            socket.on('disconnect', () => {
                try {
                    const index = users.findIndex(user => user.id === socket.id);
                    if (index !== -1) {
                        users[index].id = null;
                        const disconnectedUser = users[index].userName;
                        io.emit('userList', { 
                            data: disconnectedUser,
                            action: 'disconnected'
                        });
                        console.log(`User ${disconnectedUser} disconnected`);
                    }
                } catch (error) {
                    console.log(error);
                }
            });

            socket.on('getOfflineMessages', async (userName, userUUID) => {
                try {
                    if (isCorrectUser(userName, userUUID)) {
                        const user = findUserByName(userName);
                        const messages = await db.getMessages(user.uuid);
                        socket.emit('offlineMessages', {
                            data: messages,
                            action: 'offlineMessages',
                        });
                    }
                } catch (error) {
                    console.log(error);
                }
            });

            socket.on('offlineReceived', async (userName, userUUID) => {
                try {
                    if (isCorrectUser(userName, userUUID)) {
                        const user = findUserByName(userName);
                        db.clearTable(user.uuid);
                    }
                } catch (error) {
                    console.log(error);
                }
            });

            function sendUserListToSender(socketID) {
                try {
                    const userList = users.filter(user => user.id !== socket.id).map(user => ({
                        userName: user.userName,
                        isOnline: !!user.id
                    }));
                    io.to(socketID).emit('init', { 
                        data: userList,
                        action: 'init' 
                    });
                } catch (error) {
                    console.log(error);
                }
            }
        });

        server.listen(PORT, () => {
            console.log(`Server running on http://localhost:${PORT}`);
        });
    } catch (error) {
        console.log(error);
    }
}

main();