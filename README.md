# Distributed Applications - WS 2023/2024
This repository contains source code for the **Distributed Applications** module in the Winter Semester 2023/2024 at Hochschule Fulda.

## Environment

The project uses 2 servers, one for the database and one for the application, as well as a key vault to store the database credentials. They are all hosted on Microsoft Azure.

Both servers run Ubuntu 20.04 LTS.

## Setup

### Database

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

#### Preparing the MySQL Server

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

#### Reverting the root password (optional)

After the MySQL Secure Installation wizard has finished, we can revert the root password to the auth_socket plugin:

**_NOTE:_** You need to provide the password you set in the previous step when logging into MySQL for this step, but won't need it after changing it back to auth_socket.

```bash
mysql -u root -p

ALTER USER 'root'@'localhost' IDENTIFIED WITH auth_socket;

exit
```

#### Creating the database and table

We will create a database called bmi_db and a table called bmi.

**_NOTE:_** You can use any name you want for the database and table, but you need to change the names accordingly.

```bash
sudo mysql

CREATE DATABASE bmi_db;

CREATE TABLE `bmi_db`.`bmi`(`id` INT AUTO_INCREMENT NOT NULL, `name` VARCHAR(45) NOT NULL, `weight` DOUBLE NOT NULL, `height` DOUBLE NOT NULL, `bmi` DOUBLE NULL, PRIMARY KEY (`id`));

Check to see if table is correctly configured:
desc `bmi_db`.`bmi`;

exit;
```

#### Create external db user

We will create a user called dbuser with a password and grant it access to the database bmi_db. We will also grant it the permissions to select, insert and alter the table bmi. This user will be used by the application to access the database. The host % allows the user to connect from any IP address.

**_NOTE:_** You can use any name you want for the user, but you need to change the name accordingly.

**_IMPORTANT:_** You need to change the password to a strong password of your choice. It is recommended to set the host to the IP address of your application server, if you want to restrict access to the database to only that server.

```bash
sudo mysql

CREATE USER 'dbuser'@'%' IDENTIFIED BY 'password';

GRANT SELECT, INSERT, ALTER ON `bmi_db`.`bmi` TO 'dbuser'@'%';

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

### Creating a key vault

To create a key vault, you need to access the azure portal and navigate to your resource group. Click on **Create** and search for **Key Vault**. Alternatively, you can also search for it in the search bar at the top of the page. Make sure that you're using the correct subscription and resource group, as well as choosing Access control (IAM) when creating the **Key Vault**.

**_Note:_** You most likely can't create a secret in the **Key Vault**, as you need to assign the administrator role to yourself, even if you yourself created the vault. 

##### Assigning the administrator role

Go to your vault, click in the navigation on **Access control (IAM)** and go to the tab **Check access**.
From here, you can click on the button **Add role assignment** and assign the role **Key Vault Administrator** to yourself.

In the new opened view (on tab **Role**) click on **Key Vault Administrator**.
Click **Next** and you will change to the **Members** tab.
Check that the option for **Assign access to** is set to **User, group, or service principal**.
Now click **Select members**, enter your email address, select your entry and click on the button **Select**.
Click on **Review + assign** to confirm that you added your account to the vault, then click **Review + assign** again to finally assign it.

### Creating the secrets

To create the secrets, click on **Secrets** in the navigation and then on **Generate/Import**.

Simply add the **Name** and the **Secret value** to the entry and make sure that it is enabled.

You will need to create 3 secrets:
- **DBUri** to store the IP address of your database server and the name of your database.
- **DBUsername** to store the username of your database user.
- **DBPassword** to store the password of your database user.

You will fetch the secret with the provided name in your application.

**_NOTE:_** In your **application.properties**, you will find placeholders like **\$\{DBUsername\}**. Replace the value inside the brackets to match the name you've selected for your secrets. For your **\$\{DBUri\}**, you need to replace the IP address with the IP address of your database server and the name of your database. E.g.: **127.0.0.1/bmi_db**

**_CAUTION:_**
As the student version of azure does not allow you to register applications, you can't use an **Application ID** to access the **Key Vault**. You also can't use **Microsoft Entra** to get a device code for connection, as it is deactivated by the administrator as well. Therefore, we will use **Managed identities** for productive use and [Azure CLI](https://learn.microsoft.com/en-us/cli/azure/install-azure-cli) for development.

### Login to Azure CLI to authenticate your application

To test your application, you need to install the [Azure CLI](https://learn.microsoft.com/en-us/cli/azure/install-azure-cli) first.

After installing it, open a command line and type:

```bash	
az login
```

Your browser will open - login with your credentials if needed.

Your application will now authenticate you automatically - if you have adjusted your application.properties correctly, you can now run your application from Eclipse.

**_NOTE:_** If you get an error when starting your application, you may need to clean your project first. (under Eclipse: **Project** -> **Clean**)

### Building your project

To build your project for exporting it, open your terminal or command line and navigate to the root of your project.
You can find it for example in Eclipse when right-clicking your project under **Properties** -> **Resource**.
Under **Location** is the path to your project.

When you've navigated to the project root with your terminal, run the command:

```bash	
mvnw clean package
```

Your application is now being build.

**_NOTE:_**
If you get the message that the application can't find **JAVA_HOME** you need to set the environment variable to point to your Java JDK and try to run it again, after closing and opening your terminal again.

Replacing the filename in the following command with the filename of the file created by maven in your projects **target** directory, you can now run the file:

```bash
java -jar target\projectname-SNAPSHOT.jar.
```

If your application is already running, you need to stop the process, or the port is already blocked. (If everything fails, use the task manager!)

### Deploying your app

Create another server for your app.

To enable your app to access the key vault, you need to activate the **managed identity** for your VM.
In the Azure portal, access the page for your new application VM and go to **Settings** -> **Identity** and activate it (**Status: On** -> **Save**).

This will allow Azure to identify your resource.
After the resource automatically registered with Microsoft Entra ID, a new button titled **Azure role assignments** should appear.
Click it, then click **Add role assignment** in the new window.
To set the role of the resource, set following settings:

```
Scope: Key Vault

Subscription: Azure for Students

Resource: Name of your Key Vault

Role: Key Vault Secrets User
```

Save the settings.

Your application can now access the Key Vault.

### Setting up your app server

Connect to your app server and transfer your application (**jar**) to it.

#### Install Java on your VM
Download the latest version of [java](https://www.oracle.com/de/java/technologies/downloads/).

Select the correct version for your VM - You can see what architecture you VM uses on the azure page.

Transfer it to your VM as well.

The following example shows how to install jdk-21 for Linux x64 Systems:

```bash
tar -xvzf jdk-21_linux-x64_bin.tar.gz
sudo mkdir /opt/java
sudo mv jdk-21.0.1 /opt/java/
export JAVA_HOME=/opt/java/jdk-21.0.1
export PATH=$PATH:$JAVA_HOME/bin
source ~/.bashrc
```

If you want to check if the installation was successful, you can use the following command:

```bash
java -version
```

### Running your app

Now you can run your app with:
    
```bash
java -jar app.jar
```

Don't forget to allow inbound connections for the port set in the application! (**Azure Portal** -> **VM** -> **Networking**)

---

### Creating a service (optional)

You can create a service, so that your app starts when your server starts. In the following command, replace **your-app-name** with the name of your app.

```bash
sudo nano /etc/systemd/system/your-app-name.service
```

Add the following content to the file, replace placeholders and save:

```
[Unit]
Description=Your Spring Boot Application
After=syslog.target

[Service]
User=your_username_in_the_vm
ExecStart=/opt/java/jdk-21.0.1/bin/java -jar /path/to/your_jar_file.jar
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
```

You now need to reload the daemon and start the service:

```bash
sudo systemctl daemon-reload
sudo systemctl start your-app-name
sudo systemctl enable your-app-name
```

You app should now be running, you can check the status with:

```bash
sudo systemctl status your-app-name
```

### Using Nginx as a reverse proxy (optional)

You can use Nginx as a reverse proxy to serve your application on port 80 and enable https.

```bash
sudo apt install nginx
```

Create nginx config with your app name as **your-app**:

```bash
sudo nano /etc/nginx/sites-available/your-app
```

Add the following content to the file, replace placeholders and save:

```
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:8081; # Adjust the port to your Spring Boot app
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location ~ /\.ht {
        deny all;
    }
}
```

**_NOTE:_** You can set the servers ip address as **server_name** if you don't have a domain.

Link your app (change **your-app** to your app name) to the sites-enabled directory, test the configuration and restart nginx, if the test was successful:

```bash
sudo ln -s /etc/nginx/sites-available/your-app /etc/nginx/sites-enabled
sudo nginx -t
sudo systemctl restart nginx
```

You can now remove the inbound port rule for port 8081, as nginx will serve it on port 80.

### Using SSL with Nginx (optional)
**_NOTE:_**
You need to have a domain for your server!
If you only have an ip address, **certbot / letsencrypt** won't provide a certificate!
You can also use a self signed certificate (explained further below), but as it is not safe, it isn't recommended.

#### Use certbot to obtain SSL Certificate

Replace placeholders and install certbot:

```bash
sudo apt install certbot
sudo certbot --nginx -d your-domain.com
```

You now need to update your nginx config to use the certificate:

```bash
sudo nano /etc/nginx/sites-available/your-app
```

Replace the content with this new config file (and replace the placeholders like server_name and ssl_certificate with your own values):

```
server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl;
    server_name your-domain.com;

    ssl_certificate /etc/letsencrypt/live/your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers 'TLS_AES_128_GCM_SHA256:TLS_AES_256_GCM_SHA384:TLS_CHACHA20_POLY1305_SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384';

    location / {
        proxy_pass http://localhost:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location ~ /\.ht {
        deny all;
    }
}
```

If the path to the certificates provided by certbot differs from the config, adjust accordingly.

Test if configuration is correct:

```bash	
sudo nginx -t
```

And restart nginx:

```bash
sudo systemctl restart nginx
```

#### Self Signed Certificate (For testing purposes only, not recommended for production):

You can use a self signed certificate for testing purposes, but it is not recommended for production. Accessing the website will show a warning in the browser, as the certificate is not trusted.
You can use the following commands to create a self signed certificate:

```bash
sudo mkdir -p /etc/nginx/ssl
sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout /etc/nginx/ssl/self-signed.key -out /etc/nginx/ssl/self-signed.crt
```

Provide data as needed. You can also leave the fields empty.

Update nginx config:
```bash
sudo nano /etc/nginx/sites-available/your-app
```

Replace the content with this new config file (and replace the placeholders):

```
server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl;
    server_name your-domain.com;

    ssl_certificate /etc/nginx/ssl/self-signed.crt;
    ssl_certificate_key /etc/nginx/ssl/self-signed.key;

    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers 'TLS_AES_128_GCM_SHA256:TLS_AES_256_GCM_SHA384:TLS_CHACHA20_POLY1305_SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384';

    location / {
        proxy_pass http://localhost:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location ~ /\.ht {
        deny all;
    }
}
```

Test if configuration is correct:
```bash
sudo nginx -t
```

Restart nginx:
```bash
sudo systemctl restart nginx
```

### Using the application

You can now access your application via your domain or ip address.
Entering you data will calculate your BMI and save it to the database.