# Node Server
* node server is used for user discovery via websockets and for sending messages between users
* written in JavaScript using the [Express](https://expressjs.com/) framework

* Azure Virtual Machine hosting the node server, running on Ubuntu 22.04.

## Installing Node and NPM
* update package manager if needed
```bash
sudo apt update
```
* install Node and NPM.
```bash
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash - &&\
sudo apt-get install -y nodejs
```

When you're asked to restart services, confirm the select ones with enter.

## Copying the Server Files
* use tools like WinSCP or FileZilla to do this. 
* copy the files to "/home/azureuser/server" and then move them to "/var/node/server"
```bash
sudo mkdir /var/node
sudo mkdir /var/node/server
sudo mv /home/azureuser/server/* /var/node/server
```

## Installing Dependencies of the Server
* [Socket.IO](https://www.npmjs.com/package/socket.io) package for websockets 
* [express](https://www.npmjs.com/package/express) package for the web server. 
* install dependencies with "npm install":
```bash
cd /var/node/server
sudo npm install
```

## Running the Server
* the server will run with root privileges (not recommended, security issues)
* create a new user to run the server
```bash
sudo adduser node
```

* change the ownership of the server files.
```bash
sudo chown -R node:node /var/node/server
```

* (optional) change the port that the server runs on: Change "port" variable in "csconfig.js" (default 8081). 
* run the server:
```bash
su node
cd /var/node/server
node server.js
```

## Opening the Port in the Firewall
* allow server connections -> open port in the firewall
* open azure portal and navigate to the virtual machine
* click on Networking and add an inbound port rule for the port that the server is running on 
* e.g.: server is running on port 8081 -> add a rule for port 8081.

## Stop the server
* stop the server:
```bash
# as user node
pm2 stop server.js
```
* check server status:
```bash
# as user node
sudo pm2 status

# as root
sudo systemctl status pm2-node
```