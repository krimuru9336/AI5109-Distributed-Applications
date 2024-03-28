/**
 * This file represents the server-side code for a distributed application.
 * It uses Express.js and Socket.IO to handle HTTP requests and real-time communication.
 * The server interacts with a database using the `database_connection` module.
 * The code defines various functions for user authentication, message handling, and group management.
 * It also includes event listeners for socket connections and media transfer.
 */
const express = require('express');
const http = require('http');
const socketIo = require('socket.io');
const validator = require('validator');
const crypto = require('crypto');
const dotenv = require('dotenv');
const db = require('./database_connection');

dotenv.config();

/**
 * The main function that initializes the server and sets up the necessary configurations.
 * 
 * @returns {Promise<void>} - A promise that resolves when the server is successfully started.
 */
async function main() {
    try {
        const app = express();
        const server = http.createServer(app);
        const io = socketIo(server);

        await db.init();

        const PORT = process.env.PORT;

        // Get all users from the database
        const users = await db.getAllUsers();
        // Create a map of hashed user UUIDs to user names
        const hashUser = createHashUserMap(users);
        // Create a map of chat groups to users and admins
        const chatGroups = await createGroupMap();

        /**
         * Creates a hash with salt by concatenating the given strings with a salt value.
         * 
         * @param {...string} args - The strings to be concatenated with the salt.
         * @returns {string} - The resulting hash value in hexadecimal format.
         */
        function createHashWithSalt(...args) {
            const salt = process.env.SALT;
            const hash = crypto.createHash('sha256');
            args.forEach(str => {
                hash.update(str + salt);
            });
            return hash.digest('hex');
        }

        /**
         * Checks if the given user name and UUID combination is correct.
         * 
         * @param {string} userName - The user name to check.
         * @param {string} userUUID - The UUID of the user to check.
         * @returns {boolean} - Returns true if the user name and UUID combination is correct, otherwise false.
         */
        function isCorrectUser(userName, userUUID) {
            return (userHashExists(userName, userUUID) && 
                hashUser.get(createHashWithSalt(userName, userUUID)) === userName);
        }

        /**
         * Checks if a user hash exists in the hashUser map.
         * 
         * @param {string} userName - The name of the user.
         * @param {string} userUUID - The UUID of the user.
         * @returns {boolean} - Returns true if the user hash exists, false otherwise.
         */
        function userHashExists(userName, userUUID) {
            return hashUser.has(createHashWithSalt(userName, userUUID));
        }

        /**
         * Creates a hash map of user UUIDs and usernames.
         * 
         * @param {Array} users - An array of user objects.
         * @returns {Map} - A map containing user UUIDs as keys and usernames as values.
         */
        function createHashUserMap(users) {
            let map = new Map();
            users.forEach(user => {
                map.set(user.uuid, user.userName);
            });
            return map;
        }

        /**
         * Creates a group map based on the database group entries.
         * 
         * @returns {Map} The group map with group names as keys and corresponding user information as values.
         */
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
            return map;
        }

        /**
         * Finds a user by their username.
         * 
         * @param {string} userName - The username to search for.
         * @returns {object|undefined} - The user object if found, or undefined if not found.
         */
        function findUserByName(userName) {
            return users.find(user => user.userName === userName);
        }

        /**
         * Finds a user by their socket ID.
         * 
         * @param {string} socketID - The socket ID of the user.
         * @returns {object|undefined} - The user object if found, otherwise undefined.
         */
        function findUserBySocketID(socketID) {
            return users.find(user => user.id === socketID);
        }

        /**
         * Adds a message to the database.
         * 
         * @param {string} sender - The sender of the message.
         * @param {object} messageObject - The message object to be added.
         * @returns {Promise<void>} - A promise that resolves when the message is added to the database.
         * @throws {Error} - If an error occurs while adding the message to the database.
         */
        async function addMessageToDatabase(sender, messageObject) {
            try {
                if (!messageObject.partnerName || !sender)
                    return;

                const user = findUserByName(messageObject.partnerName);
                messageObject.partnerName = sender.userName;
                if (messageObject.timestampEdit) {
                    await db.addEditMessage(user.uuid, messageObject);
                    return;
                } else if (messageObject.deleted) {
                    messageObject.messageText = "Message has been deleted.";
                    await db.addDeleteMessage(user.uuid, messageObject);
                } else {
                    await db.addMessage(user.uuid, messageObject);
                }

                if (messageObject.chatGroup != null) {
                    db.addGroupToMessage(user.uuid, messageObject);
                }
            } catch (error) {
                console.log(error);
            }
        }

        /**
         * Adds a user to a group in the database.
         * 
         * @param {string} groupName - The name of the group.
         * @param {string} userName - The name of the user.
         * @param {boolean} isAdmin - Indicates whether the user is an admin of the group.
         */
        function addUserToGroupDB(groupName, userName, isAdmin) {
            try {
                const user = findUserByName(userName);
                if (user) {
                    db.addUserToGroup(user.uuid, groupName, isAdmin);
                }
            } catch (error) {
                console.log(error);
            }
        }

        /**
         * Removes a user from a group in the database.
         * 
         * @param {string} groupName - The name of the group.
         * @param {string} userName - The name of the user.
         */
        function removeUserFromGroupDB(groupName, userName) {
            try {
                const user = findUserByName(userName);
                if (user) {
                    db.removeUserFromGroup(user.uuid, groupName);
                }
            } catch (error) {
                console.log(error);
            }
        }

        /**
         * Handles the websocket connection using Socket.IO.
         * 
         * @param {object} socket - The socket object representing the connection.
         */
        io.on('connection', (socket) => {
            /**
             * Handles the user check event to check if a user exists.
             * 
             * @param {string} userName - The name of the user to check.
             * @param {string} userUUID - The UUID of the user to check.
             */
            socket.on('userCheck', (userName, userUUID) => {
                try {
                    let userUnavailable = false;
                    let checkUsername = true;
                    if (userUUID != undefined && validator.isUUID(userUUID)) {
                        // Check if the user name and UUID combination is correct
                        if (isCorrectUser(userName, userUUID)) {
                            userUnavailable = false;
                            checkUsername = false;
                        }
                    }
                    // Check if the user name is already taken
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

            /**
             * Handles the user registration event to register a new user.
             * Will also tell connected users that a new user has connected.
             * 
             * @param {string} userName - The name of the user to register.
             * @param {string} userUUID - The UUID of the user to register.
             */
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
                        // Get the user object from the users array
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

            /**
             * Handles the message when media is sent.
             * Will change the message to an offline message if the recipient is offline.
             * 
             * @param {object} message - The message object containing media information.
             */
            socket.on('mediaStart', async (message) => {
                try {
                    const offlineMessage = "Looks like you were offline when media was sent. " +
                        "It wasn't saved, but feel free to ask the sender to resend it.";
                    sendMessage(message, socket, offlineMessage);
                } catch (error) {
                    console.log(error);
                }
            });

            /**
             * Handles the media chunk event when a chunk of media is received.
             * Will send the media chunk to the target user or group.
             * 
             * @param {object} message - The message object containing the media chunk information.
             */
            socket.on('mediaChunk', async (message) => {
                const { targetUserId, messageId, chunk, offset, isGroup } = message;
                if (isGroup) {
                    sendGroupChunk(targetUserId, messageId, chunk, offset, socket);
                } else {
                    const targetUser = findUserByName(targetUserId);

                    if (targetUser) {
                        // If the target user is found, send the media chunk to that users socket id
                        if (targetUser.id) {
                            io.to(targetUser.id).emit('mediaChunk', {
                                data: {
                                    messageId,
                                    chunk,
                                    offset,
                                },
                                action: 'mediaChunk',
                            });
                        }
                    }
                }
            });

            /**
             * Handles the media end event when the media transfer is complete.
             * Will send mimeType and chunkCount for verification on client side.
             * 
             * @param {object} message - The message object containing the media end information.
             */
            socket.on('mediaEnd', async (message) => {
                const { targetUserId, messageId, isGroup, chunkCount, mimeType } = message;
                const senderUser = findUserBySocketID(socket.id);
                const senderUserId = senderUser?.userName;

                if (isGroup) {
                    sendGroupMediaEnd(targetUserId, senderUserId, messageId, chunkCount, mimeType, socket);
                } else {
                    const targetUser = findUserByName(targetUserId);

                    if (targetUser) {
                        if (targetUser.id) {
                            io.to(targetUser.id).emit('mediaEnd', {
                                data: {
                                    messageId,
                                    senderUserId,
                                    mimeType,
                                    chunkCount,
                                    timestamp: Date.now(),
                                },
                                action: 'mediaEnd',
                            });
                        }
                    }
                }
            });

            /**
             * Sends a media chunk to a group of users.
             * 
             * @param {string} chatGroup - The name of the chat group.
             * @param {string} messageId - The ID of the message.
             * @param {string} chunk - The media chunk data.
             * @param {number} offset - The offset of the media chunk.
             * @param {object} socket - The socket object representing the connection.
             */
            function sendGroupChunk(chatGroup, messageId, chunk, offset, socket) {
                // Send the media chunk to all users in the chat group
                socket.to(chatGroup).emit('mediaChunk', {
                    data: {
                        messageId,
                        chunk,
                        offset,
                    },
                    action: 'mediaChunk',
                });
            };

            /**
             * Sends a media end message to a group of users.
             * 
             * @param {string} chatGroup - The name of the chat group.
             * @param {string} senderUserId - The ID of the sender user.
             * @param {string} messageId - The ID of the message.
             * @param {number} chunkCount - The total number of media chunks.
             * @param {string} mimeType - The MIME type of the media.
             * @param {object} socket - The socket object representing the connection.
             */
            function sendGroupMediaEnd(chatGroup, senderUserId, messageId, chunkCount, mimeType, socket) {
                socket.to(chatGroup).emit('mediaEnd', {
                    data: {
                        messageId,
                        senderUserId,
                        mimeType,
                        chunkCount,
                        chatGroup,
                        timestamp: Date.now(),
                    },
                    action: 'mediaEnd',
                });
            };

            /**
             * Handles the message event when a message is received.
             * 
             * @param {object} message - The message object containing the message information.
             */
            socket.on('message', (message) => {
                try {
                    sendMessage(message, socket);
                } catch (error) {
                    console.log(error);
                }
            });

            /**
             * Sends a message to the target user or group.
             * 
             * @param {object} message - The message object containing the message information.
             * @param {object} socket - The socket object representing the connection.
             * @param {string} offlineReplacer - The message to replace the original message if the recipient is offline.
             */
            function sendMessage(message, socket, offlineReplacer = null) {
                let { targetUserId, messageId, message: messageText } = message;
                const senderUser = findUserBySocketID(socket.id);
                const senderUserId = senderUser?.userName;

                // Send the message to the target group
                if (message.isGroup) {
                    sendGroupMessage(targetUserId, senderUser, messageId, messageText, socket, offlineReplacer);
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
                            // Replace the message if the target user is offline
                            if (offlineReplacer != null) {
                                messageText = offlineReplacer;
                            }
                            // If the target user is offline, add the message to the database
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
                // Update the timestamp for the sender to synchronize the correct time
                io.to(socket.id).emit('timestamp', {
                    data: {
                        messageId,
                        timestamp: Date.now(),
                    },
                    action: 'timestamp',
                });
            }

            /**
             * Sends a message to a group of users.
             * 
             * @param {string} chatGroup - The name of the chat group.
             * @param {object} senderUser - The user object of the sender.
             * @param {string} messageId - The ID of the message.
             * @param {string} messageText - The text of the message.
             * @param {object} socket - The socket object representing the connection.
             * @param {string} offlineReplacer - The message to replace the original message if the recipient is offline.
             */
            function sendGroupMessage(chatGroup, senderUser, messageId, messageText, socket, offlineReplacer = null) {
                const senderUserId = senderUser?.userName;
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
                // Add the message to the database for offline users
                // Needs to be done for each offline user in the group
                for (const user of getOfflineGroupMembers(chatGroup)) {
                    if (offlineReplacer != null) {
                        messageText = offlineReplacer;
                    }
                    addMessageToDatabase(senderUser, {
                        partnerName: user,
                        incoming: true,
                        messageText,
                        timestamp: Date.now(),
                        id: messageId,
                        chatGroup
                    });
                }
            }

            /**
             * Gets the offline members of a chat group.
             * 
             * @param {string} chatGroup - The name of the chat group.
             * @returns {string[]} - An array of offline user names.
             */
            function getOfflineGroupMembers(chatGroup) {
                return chatGroups.get(chatGroup).users.filter(user => !findUserByName(user).id);
            }

            /**
             * Lets users join a chat group.
             * Checks if the chat group exists and if the user is part of the group.
             * 
             * @param {string} chatGroup - The name of the chat group.
             * @param {string} userName - The name of the user joining the chat group.
             */
            socket.on('joinChatGroup', (chatGroup, userName) => {
                if (chatGroups.has(chatGroup) && chatGroups.get(chatGroup).users.includes(userName)) {
                    socket.join(chatGroup);
                }
            });

            /**
             * Lets a socket join a chat group.
             * 
             * @param {string} chatGroup - The name of the chat group.
             */
            socket.on('socketJoinGroup', (chatGroup) => {
                socket.join(chatGroup);
            });

            /**
             * Lets a socket leave a chat group.
             * 
             * @param {string} chatGroup - The name of the chat group.
             */
            socket.on('socketLeaveGroup', (chatGroup) => {
                socket.leave(chatGroup);
            });

            /**
             * Creates a chat group if it does not already exist.
             * 
             * @param {string} chatGroup - The name of the chat group.
             * @param {string} userName - The name of the user / admin creating the chat group.
             */
            socket.on('createChatGroup', (chatGroup, userName) => {
                if (createChatGroupIfNotExists(chatGroup, userName)) {
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

            /**
             * Updates a chat group with new users.
             * Includes adding and removing users from the group.
             * 
             * @param {string} chatGroup - The name of the chat group.
             * @param {string[]} userNames - The names of the users to add or remove.
             */
            socket.on('updateChatGroup', (chatGroup, userNames) => {
                const group = chatGroups.get(chatGroup);
                // Check if the user is the admin of the group
                if (group.admin !== findUserBySocketID(socket.id).userName) {
                    // Send an unauthorized message to the user if they are not the admin
                    io.to(socket.id).emit('unauthorizedGroup');
                    return;
                };
                userNames = JSON.parse(userNames);
                // Add users to the group and remove users from the group
                const usersToAdd = userNames.filter(user => !group.users.includes(user));
                const usersToRemove = group.users.filter(user => {
                    return user !== group.admin && !userNames.includes(user);
                });
                addUsersToChatGroup(chatGroup, usersToAdd);
                removeUsersFromChatGroup(chatGroup, usersToRemove);
                // Let the users know they have joined or left the group
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

            /**
             * Get all groups that a user is part of.
             * 
             * @param {string} userName - The name of the user.
             */
            socket.on('getGroups', (userName) => {
                // Get all groups that the user is part of
                const userGroups = Array.from(chatGroups.entries()).filter(([_, group]) => group.users.includes(userName)).map(([group, _]) => group);
                socket.emit('groups', {
                    data: userGroups,
                    action: 'groups',
                });
                // Join the user to the groups
                for (const group of userGroups) {
                    socket.join(group);
                }
            });

            /**
             * Get all users in a chat group.
             * 
             * @param {string} chatGroup - The name of the chat group.
             */
            socket.on('usersInGroup', (chatGroup) => {
                const group = chatGroups.get(chatGroup);
                if (group) {
                    socket.emit('usersInGroup', {
                        data: group.users,
                        action: 'usersInGroup',
                    });
                }
            });

            /**
             * Creates a chat group if it does not already exist.
             * 
             * @param {string} chatGroup - The name of the chat group.
             * @param {string} userName - The name of the user creating the chat group.
             * @returns {boolean} - Returns true if the chat group was created, false otherwise.
             */
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

            /**
             * Removes users from a chat group.
             * 
             * @param {string} chatGroup - The name of the chat group.
             * @param {string[]} userNames - The names of the users to remove.
             */
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

            /**
             * Adds users to a chat group.
             * 
             * @param {string} chatGroup - The name of the chat group.
             * @param {string[]} userNames - The names of the users to add.
             */
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

            /**
             * Handles the event when a message is deleted.
             * 
             * @param {object} message - The message object containing the delete information.
             */
            socket.on('delete', (message) => {
                try {
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
                                // If the target user is offline, add the message to the database
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

            /**
             * Sends a delete message to a group of users.
             * 
             * @param {string} chatGroup - The name of the chat group.
             * @param {string} senderUserId - The ID of the sender user.
             * @param {string} messageId - The ID of the message.
             * @param {object} socket - The socket object representing the connection.
             */
            function deleteGroupMessage(chatGroup, senderUserId, messageId, socket) {
                socket.to(chatGroup).emit('delete', {
                    data: {
                        senderUserId,
                        messageId,
                        chatGroup,
                    },
                    action: 'delete',
                });
            }

            /**
             * Handles the event when a message is edited.
             * 
             * @param {object} message - The message object containing the edit information.
             */
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
                                // If the target user is offline, add the message to the database
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
                    // Update the timestamp for the sender to synchronize the correct time
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

            /**
             * Sends an edit message to a group of users.
             * 
             * @param {string} chatGroup - The name of the chat group.
             * @param {string} senderUserId - The ID of the sender user.
             * @param {string} messageId - The ID of the message.
             * @param {string} messageText - The text of the message.
             * @param {object} socket - The socket object representing the connection.
             */
            function editGroupMessage(chatGroup, senderUserId, messageId, messageText, socket) {
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

            /**
             * Handles the event when a user disconnects.
             * Will notify other users that a user has disconnected.
             */
            socket.on('disconnect', () => {
                try {
                    // Find the user in the users array and set their socket ID to null
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

            /**
             * Gets the offline messages for a user from database.
             * 
             * @param {string} userName - The name of the user.
             * @param {string} userUUID - The UUID of the user.
             */
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

            /**
             * Deletes all offline messages for a user from database.
             * Only triggers if correct user received the messages
             * 
             * @param {string} userName - The name of the user.
             * @param {string} userUUID - The UUID of the user.
             */
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

            /**
             * Sends the user list to the sender.
             * 
             * @param {string} socketID - The socket ID of the sender.
             */
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

// Start the server
main();