# ChitChat Application Client

* ChitChat application is written using Android Studio (XML frontend and Java backend). 
* Client uses [Socket.IO](https://socket.io/) library for websockets (socket.io-client:2.1.0)
* Android Studio requires Android SDK, tested Android version is 14 ("UpsideDownCake") and API Level 34.
* a node server is used as a user discovery and communication server (for the websockets)

## Building the Client
* install Android SDK (prompts when opening Android Studio)
* install Android SDK Building Tools (open SDK Manager in Android Studio and install SDK Building Tools, API Level 34)
* change IP address in "app/res/values/strings.xml" to IP of the communication server (not localhost -> simulators do not work then).

## Running the Client
* connect Android device (WiFi or USB cable with USB debugging in developer settings)
* click green play button

### Alternatively: APK
* build an APK file to install on your device
* Build > Build Bundle(s) / APKS(s) > Build APK(s)
* Path to the file: "app/build/outputs/apk/debug/app-debug.apk"

### Alternatively: Virtual Device
* create Virtual Device in ANdroid Studio
* run app by clicking green play button

## Use the Application

### Connect to ChitChat
* press the oval button "Connect" in order to connect to the application.
* you do not need to choose a username, the communication server will generate one for you
  * the username will be generated from a combination of an positive adjective and an animal name
  * the overall posibilities are 100x100 = 10000 different, unique usernames
  * when a user disconnects from the app, the username will be freed
  
### Find chatpartners
* after getting a username and automatically registering with it, the user will see its own username at the top and beneath an overview of possible chatpartners
* when a new user logs in or disconnects, this list and the user count will automatically update
* to start chatting, click on a username

### Sending / Reading messages
* when you clicked on a username, you will get to the chatpartners
* the chatpartners name is displayed on top
* click on the type-input field, write your message and click the arrow button to send your message
* the message is then sent to your chatpartner and will be displayed immediately in the chat view
* the message includes the senders username and timestamp, when the message was written
* to return to the users overview, click the arrow on the top left

### Editing / Deleting messages
* when clicking and holding on a message, you can choose different actions for your message by clicking onto them
* you can delete incoming messages for yourself
* you can delete outgoing (your) messages for yourself, for all and also delete them
* the timestamp of editing/deleting will be shown
* deleted messages cannot be edited, deleted or restored anymore

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