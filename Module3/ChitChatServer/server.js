const express = require('express');
const http = require('http');
const config = require('./config');
const { send } = require('process');
const app = express();
const server = http.createServer(app);
const io = require('socket.io')(server, { allowEIO3: true });


const PORT = config.port;

const connectedUsers = [];

io.on('connection', (socket) => {
    console.log("connection rcvd");
    // Check if a user with the given username already exists
    socket.on('userCheck', (username) => {
        try {
            console.log("userCheck");
            let userExists = connectedUsers.some(user => user.userId === username);
            socket.emit('userExists', {
                data: userExists,
                name: username,
                action: 'userExists'
            });
        } catch (error) {
            console.log(error);
        }
    });

    // Handle registration of a new user
    socket.on('registerUser', (username) => {
        try {
            // send user list to new user
            // io.to(socketID).emit('init', connectedUsers.map(user => user.userId));
            sendUserListToSocket(socket.id);

            connectedUsers.push({ socketId: socket.id, userId: username });

            // Broadcast to other users that a new user has connected
            socket.broadcast.emit('user', {
                data: username,
                action: 'connected'
            });

            console.log(`User ${username} connected`);
        } catch (error) {
            console.log(error);
        }
    });

    // Handle user disconnection
    socket.on('disconnect', () => {
        try {
            const index = connectedUsers.findIndex(user => user.socketId === socket.id);
            if (index !== -1) {
                const disconnectedUser = connectedUsers[index].userId;
                connectedUsers.splice(index, 1);

                // Broadcast to other users that a user has disconnected
                io.emit('user', {
                    data: disconnectedUser,
                    action: 'disconnected'
                });

                console.log(`User ${disconnectedUser} disconnected`);
            }
        } catch (error) {
            console.log(error);
        }
    });

    // Handle incoming messages
    socket.on('message', (message) => {
        try {
            const { targetUserId, message: messageText, id, type } = message;
            console.log(message);
            const senderUserId = connectedUsers.find(user => user.socketId === socket.id)?.userId;

            // Find the socket ID of the target user
            const targetSocketId = connectedUsers.find(({ userId }) => userId === targetUserId);

            // If the target user is found, send the message to that user
            if (targetSocketId) {
                io.to(targetSocketId.socketId).emit('message', {
                    data: {
                        senderUserId,
                        message: messageText,
                        id: id,
                        type: type,
                    },
                    action: 'message',
                });
            }
        } catch (error) {
            console.log(error);
        }
    });

    function sendUserListToSocket(socketID) {
        try {
            console.log("Sending Userlist");
            const userList = connectedUsers.map(user => user.userId);
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
