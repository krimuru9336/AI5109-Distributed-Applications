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
			console.log(usernameDest);
			console.log(messageContent);
			const usernameSource = currentUsers.find(currentUser => currentUser.socket_id === socket.id)?.user_name;
			const socketidDest = currentUsers.find(currentUser => currentUser.user_name === usernameDest)?.socket_id;
			if(socketidDest){
				sio.to(socketidDest).emit('message',{
					data: {
						usernameSource: usernameSource,
						message: messageContent,
						timestamp: timestamp,
					},
					action:'message',
				});
			}
		}
		catch(error){
			console.log("Message.");
			console.log(error);
		}
	});
	
});

// Start the server
server.listen(port, () => {
  console.log(`Server is running on http://localhost:${port}`);
});
