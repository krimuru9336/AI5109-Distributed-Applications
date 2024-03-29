# Node Server
The node server is used for user discovery (so that the user can find other users to chat with) and for sending messages between users. The node server is written in JavaScript and uses the [Express](https://expressjs.com/) framework. It will also communicate with a MySQL database to store messages for offline users general information what names are already taken and what groups are created. For media it will simply pass the media (as chunks) through to the other user, without storing it.

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

## Changing the .env file
The server uses a `.env` file to store the database connection information, as well as a salt used to encrypt usernames. You can copy the `.env.example` file to `.env` and change the values to match your configuration.
```bash
cp .env.example .env
nano .env
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

# Database

The database server is used to store the messages between users when they are offline (which will be deleted on delivery when they're online again), the used usernames and the created groups. We will be using MySQL as the database server.

We use the tutorial from [DigitalOcean](https://www.digitalocean.com/community/tutorials/how-to-install-mysql-on-ubuntu-22-04) to set up the database server.

First, you need to update your package index and install the MySQL server package:

```bash
sudo apt update
sudo apt install mysql-server
sudo systemctl start mysql.service
sudo systemctl enable mysql.service
```

To check if your MySQL server is running, you can use the following command:

```bash
sudo systemctl status mysql.service
```

## Preparing the MySQL Server

We will be using the MySQL Secure Installation wizard to improve the security of our MySQL installation. We need to change the root password temporarily (to access this script) and remove the anonymous user, disable remote root login, and remove the test database and access to secure our database server.

In the following command, replace the word password with a strong password of your choice:

```bash
sudo mysql

ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'password';

exit
```

Now we can run the MySQL Secure Installation wizard:

```bash
sudo mysql_secure_installation
```

## Reverting the root password (optional)

After the MySQL Secure Installation wizard has finished, we can revert the root password to the auth_socket plugin:

**_NOTE:_** You need to provide the password you set in the previous step when logging into MySQL for this step, but won't need it after changing it back to auth_socket.

```bash
mysql -u root -p

ALTER USER 'root'@'localhost' IDENTIFIED WITH auth_socket;

exit
```

## Creating the database and table

The databases and tables are created automatically by the server.

#### Create external db user

We will create a user called chatadmin with a password and grant it access to the all databases, having all permission to insert and delete. This user will be used by the application to access the database. The host % allows the user to connect from any IP address. 

**_NOTE:_** You can use any name you want for the user, but you need to change the name accordingly. We are also only using the databases chatDB and messageDB, which are created on the first run of the server. You can change the permissions to only allow access to these databases if you want.

**_IMPORTANT:_** You need to change the password to a strong password of your choice. It is recommended to set the host to the IP address of your application server, if you want to restrict access to the database to only that server.

```bash
sudo mysql

CREATE USER 'chatadmin'@'%' IDENTIFIED BY 'password';

GRANT ALL PRIVILEGES ON *.* TO 'chatadmin'@'%';

FLUSH PRIVILEGES;
```

### Allow external connections to your db

To allow external connections to your database, you need to access the azure portal and navigate to your database server. Click on **Networking** in the navigation and add a new inbound port rule:

```
Source: IP of your other server (or any)

Destination: *

Service: MySQL

Action: Allow

Priority: 310

Name: MySQL
```

You also need to change the bind-address in the MySQL config file to allow external connections:

```bash
sudo nano /etc/mysql/mysql.conf.d/mysqld.cnf
```

Change **bind-address** and **mysqlx-bin-address** from **127.0.0.1** to **0.0.0.0** to allow the MySQL server to listen on all ips.

You need to restart the MySQL server for the changes to take effect:

```bash
sudo systemctl restart mysql
```

Now you can test if you can connect to the server on port 3306

#### On Windows

Open PowerShell and use the following command, replacing **&lt;IP address&gt;** with the IP address of your database server:

```bash
Test-NetConnection -ComputerName <IP address> -Port 3306
```

If you configured your server correctly, the test should succeed.
