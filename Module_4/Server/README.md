# Node Server
The node server is used for user discovery (so that the user can find other users to chat with) and for sending messages between users. The node server is written in JavaScript and uses the [Express](https://expressjs.com/) framework.

We will be using an Azure Virtual Machine to host the node server. The virtual machine is running Ubuntu 22.04.

## Installing Node and NPM
After connecting to the server, we need to install Node. If you can't install it, you may need to update the package manager first.
```bash
sudo apt update
```
Then, install Node and NPM.
```bash
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash - &&\
sudo apt-get install -y nodejs
```

When you're asked to restart services, confirm the select ones with enter.

## Copying the Server Files
Copy the files to the server. You can use tools like WinSCP or FileZilla to do this. For this example, we will copy the files to `/home/azureuser/server` and then move them to `/var/node/server`. If the directories don't exist, you can create them with `mkdir`.
```bash
sudo mkdir /var/node
sudo mkdir /var/node/server
sudo mv /home/azureuser/server/* /var/node/server
```
Your server files should now be in `/var/node/server`.

## Installing Dependencies
The server uses the [Socket.IO](https://www.npmjs.com/package/socket.io) package for websockets and the [express](https://www.npmjs.com/package/express) package for the web server. Install the dependencies with `npm install`.
```bash
cd /var/node/server
sudo npm install
```

## Running the Server
As of now, the server will run with root privileges. This is not recommended, but it is the easiest way to get the server running.
For security reasons, you should create a new user to run the server. You can do this with `adduser`.
```bash
sudo adduser node
```

We should also change the ownership of the server files to the new user.
```bash
sudo chown -R node:node /var/node/server
```

If you want to change the port that the server runs on, you can change the `PORT` variable in `config.js`. By default, the server runs on port 8081. To run the server, you can use:
```bash
su node
cd /var/node/server
node server.js
```

## Opening the Port in the Firewall
To allow the server to receive connections, you will need to open the port in the firewall. Open the azure portal and navigate to the virtual machine. Then, click on Networking and add an inbound port rule for the port that the server is running on. For example, if the server is running on port 8081, you would add a rule for port 8081.

## Setting Up the Server to Run on Startup
If you want the server to run on startup, you can use [PM2](https://pm2.keymetrics.io/). You have to change to your root with `exit` to install PM2 with 
```bash
sudo npm install pm2 -g
```
Now you can change back to the node user with 
```bash
su node
```
Then, start the server with 
```bash
# as user node
pm2 start server.js
``` 
You can view the logs with 
```bash
# as user node
pm2 logs
```
To make PM2 run on startup, use 
```bash
# as user node
pm2 startup systemd
```
This will provide you with a command to run.
As this command requires root privileges, you will need to run it with `sudo`, requiring you to change back to root with `exit`.
It should look something like 
```bash
# as root
sudo env PATH=$PATH:/usr/bin /usr/lib/node_modules/pm2/bin/pm2 startup systemd -u node --hp /home/node
```
To remove it from startup, use 
```bash
# as user node
pm2 unstartup systemd

# which again provides a command to run as root
sudo env PATH=$PATH:/usr/bin /usr/lib/node_modules/pm2/bin/pm2 unstartup systemd -u node --hp /home/node
```
If you want to stop the server, use 
```bash
# as user node
pm2 stop server.js
```
You can check on the status of the server with 
```bash
# as user node
sudo pm2 status

# as root
sudo systemctl status pm2-node
```
