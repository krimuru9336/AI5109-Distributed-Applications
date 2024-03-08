//Simon Keller, 1165562, fdai5676
const csconfig = require('./csconfig');
const express = require('express');
const http = require('http');
const socketio = require('socket.io');
const app = express();
const server = http.createServer(app);
const port = csconfig.port;

const sio = socketio(server);

const currentUsers = [];
// Arrays of positive adjectives and animal names
const adjectives = csconfig.adjectives;
const animals = csconfig.animals;
const chatrooms = csconfig.chatrooms;
//Add chatrooms as current users
for(let i = 0; i<chatrooms.length;i++){
	currentUsers.push({user_name:chatrooms[i], socket_id:-1});
}

// Function to generate a random username
function generateRandomUsername() {
  
  let randomAdjective = adjectives[Math.floor(Math.random() * adjectives.length)];
  let randomAnimal = animals[Math.floor(Math.random() * animals.length)];

  // Combine the selected adjective and animal to form the username
  let username = `${randomAdjective} ${randomAnimal}`;
  let isTaken = currentUsers.find(currentUser => currentUser.user_name === username)?true:false;
  while(isTaken){
	randomAdjective = adjectives[Math.floor(Math.random() * adjectives.length)]; 
	randomAnimal = animals[Math.floor(Math.random() * animals.length)];
	username = `${randomAdjective} ${randomAnimal}`;
	isTaken = currentUsers.find(currentUser => currentUser.user_name === username)?true:false;
  }
  return username;
}


sio.on('connection',(socket) => {
	socket.on('getUsername',() => {
		try {
			username = generateRandomUsername();
			socket.emit('getUsername',{
				username: username,
				action: 'getUsername'
			});
		}
		catch(error){
			console.log("getUsername failed.");
			console.log(error);
		}
	});
	
	socket.on('registerUser',(username) => {
		try {
			const usernameList = currentUsers.map(currentUser => currentUser.user_name);
			sio.to(socket.id).emit('registerUser',{
				data: usernameList,
				action: 'registerUser'				
			});
			currentUsers.push({user_name:username, socket_id:socket.id});
			
			socket.broadcast.emit('userList', { 
                data: username,
                action: 'connect'
            });
            console.log(`User ${username} connected to ChitChat!`);
		}
		catch(error){
			console.log("registerUser failed.");
			console.log(error);
		}
	});
	
	socket.on('disconnect',() => {
		try {
			const indexDC = currentUsers.findIndex(currentUser => currentUser.socket_id ===socket.id);
			if(indexDC !== -1){
				const usernameDC = currentUsers[indexDC].user_name;
				currentUsers.splice(indexDC,1);
				sio.emit('userList',{
					data: usernameDC,
					action: 'disconnect'
				});
				console.log(`User ${usernameDC} disconnected from ChitChat.`);
			}
		}
		catch(error){
			console.log("disconnect failed.");
			console.log(error);
		}
	});
	
	socket.on('message',(message) => {
		try {
			const usernameDest = message.usernameDest;
			const messageContent = message.messageContent;
			const timestamp = message.timestamp;
			const msgID = message.msgID;
			console.log(usernameDest);
			console.log(messageContent);
			const usernameSource = currentUsers.find(currentUser => currentUser.socket_id === socket.id)?.user_name;
			
			if(usernameDest.includes("Chatroom")){
				socket.broadcast.emit('groupMessage',{
				data: {
						usernameSource: usernameDest,
						displayname: usernameSource,
						message: messageContent,
						timestamp: timestamp,
						msgID: msgID,
					},
					action:'message',
				});	
			}
			else{
				const socketidDest = currentUsers.find(currentUser => currentUser.user_name === usernameDest)?.socket_id;
				if(socketidDest){
					sio.to(socketidDest).emit('message',{
						data: {
							usernameSource: usernameSource,
							message: messageContent,
							timestamp: timestamp,
							msgID: msgID,
						},
						action:'message',
					});
				}
			}
		}
		catch(error){
			console.log("Message error.");
			console.log(error);
		}
	});
	
    socket.on('delete',(message) => {
		try {
			console.log("Delete");
			const usernameDest = message.usernameDest;
			const msgID = message.msgID;
			const timestamp = message.timestamp;
			if(usernameDest.includes("Chatroom")){
				socket.broadcast.emit('delete',{
					data: {
						usernameSource: usernameDest,
						timestamp: timestamp,
						msgID: msgID,
					},
					action:'delete',
				});
			}
			else{
				const usernameSource = currentUsers.find(currentUser => currentUser.socket_id === socket.id)?.user_name;
				const socketidDest = currentUsers.find(currentUser => currentUser.user_name === usernameDest)?.socket_id;
				if(socketidDest){
					sio.to(socketidDest).emit('delete',{
						data: {
							usernameSource: usernameSource,
							timestamp: timestamp,
							msgID: msgID,
						},
						action:'delete',
					});
				}
			}
		}
		catch(error){
			console.log("Delete error.");
			console.log(error);
		}
	});
	
	socket.on('edit',(message) => {
		try {
			console.log("Edit");
			const usernameDest = message.usernameDest;
			const msgID = message.msgID;
			const messageContent = message.messageContent;
			const timestamp = message.timestamp;
			if(usernameDest.includes("Chatroom")){
				socket.broadcast.emit('edit',{
					data: {
						usernameSource: usernameDest,
						timestamp: timestamp,
						msgID: msgID,
						messageContent: messageContent,
					},
					action:'edit',
				});
			}
			else{
				const usernameSource = currentUsers.find(currentUser => currentUser.socket_id === socket.id)?.user_name;
				const socketidDest = currentUsers.find(currentUser => currentUser.user_name === usernameDest)?.socket_id;
				if(socketidDest){
					sio.to(socketidDest).emit('edit',{
						data: {
							usernameSource: usernameSource,
							timestamp: timestamp,
							msgID: msgID,
							messageContent: messageContent,
						},
						action:'edit',
					});
				}
			}
			
		}
		catch(error){
			console.log("Edit error.");
			console.log(error);
		}
	});
});

// Start the server
server.listen(port, () => {
  console.log(`Server is running on http://localhost:${port}`);
});
