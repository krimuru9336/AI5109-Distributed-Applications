const express = require('express');
const http = require('http');
const socketIo = require('socket.io');
const validator = require('validator');
const config = require('./config');

const app = express();
const server = http.createServer(app);
const io = socketIo(server);

const PORT = config.port;

const users = [];

io.on('connection', (socket) => {
    socket.on('userCheck', (userId) => {
        try {
            let exists = false;
            if (users.find(user => user.userId === userId)) {
                exists = true;
            }
            socket.emit('userExists', { 
                data: exists,
                name: userId,
                action: 'userExists' 
            });
        } catch (error) {
            console.log(error);
        }
    });

    // Handle new user connection
    socket.on('registerUser', (userId) => {
        try {
            // Send updated user list to sender
            sendUserListToSender(socket.id);
            users.push({ id: socket.id, userId });
            // Send updated user list to all users expect sender
            socket.broadcast.emit('userList', { 
                data: userId,
                action: 'connected'
            });
            console.log(`User ${userId} connected`);
        } catch (error) {
            console.log(error);
        }
    });

    // Handle incoming messages
    socket.on('message', (message) => {
        try {
            const { targetUserId, message: messageText } = message;
            const senderUserId = users.find(user => user.id === socket.id)?.userId;

            // Find the socket ID of the target user
            const targetSocketId = users.find(({ userId }) => userId === targetUserId);

            // If the target user is found, send the message to that user
            if (targetSocketId) {
                io.to(targetSocketId.id).emit('message', {
                    data: {
                        senderUserId,
                        message: messageText,
                    },
                    action: 'message',
                });
            }
        } catch (error) {
            console.log(error);
        }
    });

    // Handle user disconnection
    socket.on('disconnect', () => {
        try {
            const index = users.findIndex(user => user.id === socket.id);
            if (index !== -1) {
                const disconnectedUser = users[index].userId;
                users.splice(index, 1);
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

    function sendUserListToSender(socketID) {
        try {
            const userList = users.map(user => user.userId);
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
