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
        console.log(users);
        console.log(hashUser);

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

        function addMessageToDatabase(sender, messageObject) {
            if (!messageObject.partnerName || !sender)
                return;

            const user = users.find(user => user.userName === messageObject.partnerName);
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
                    if (checkUsername && users.find(user => user.userName === userName)) {
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
                console.log("NEW ID");
                console.log(socket.id);
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

                        // Check if users array already contains the username and the hashed user
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
                    console.log(users);
                } catch (error) {
                    console.log(error);
                }
            });

            // Handle incoming messages
            socket.on('message', (message) => {
                try {
                    console.log(message);
                    const { targetUserId, messageId, message: messageText } = message;
                    const senderUser = users.find(user => user.id === socket.id);
                    const senderUserId = senderUser?.userName;

                    // Find the socket ID of the target user
                    const targetUser = users.find(({ userName }) => userName === targetUserId);
                    console.log("New Target ID:");
                    console.log(targetUser.id);

                    // If the target user is found, send the message to that user
                    if (targetUser) {
                        console.log(targetUser);
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
                        io.to(socket.id).emit('timestamp', {
                            data: {
                                messageId,
                                timestamp: Date.now(),
                            },
                            action: 'timestamp',
                        });
                    }
                } catch (error) {
                    console.log(error);
                }
            });

            // Handle deleting messages
            socket.on('delete', (message) => {
                try {
                    console.log(message);
                    const { targetUserId, messageId } = message;
                    const senderUser = users.find(user => user.id === socket.id);
                    const senderUserId = senderUser?.userName;

                    // Find the socket ID of the target user
                    const targetUser = users.find(({ userName }) => userName === targetUserId);

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
                } catch (error) {
                    console.log(error);
                }
            });

            // Handle editing messages
            socket.on('edit', (message) => {
                try {
                    console.log(message);
                    const { targetUserId, messageId, message: messageText, editDate } = message;
                    const senderUser = users.find(user => user.id === socket.id);
                    const senderUserId = senderUser?.userName;

                    // Find the socket ID of the target user
                    const targetUser = users.find(({ userName }) => userName === targetUserId);

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
                        const user = users.find(user => user.userName === userName);
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
                console.log("OFFLINE RECEIVED");
                console.log(userName);
                try {
                    if (isCorrectUser(userName, userUUID)) {
                        const user = users.find(user => user.userName === userName);
                        db.clearTable(user.uuid);
                    }
                } catch (error) {
                    console.log(error);
                }
            });

            function sendUserListToSender(socketID) {
                try {
                    const userList = users.filter(user => user.id && user.id !== socket.id).map(user => user.userName);
                    console.log(userList);
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