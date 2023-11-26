Branch by Simon Keller, fdai5676

# AI5109-Distributed-Applications

This README describes an example of necessary steps in order to solve Exercise 3.
The used technologies are:
	Springboot-App on a Azure VM
	MySQL Database und a Azure VM
	Azure's Key Vault (for credentials)
	
## Database Server

1. **Create a Azure VM and setup MySQL:**
   ```
   bash
   sudo apt update
   sudo apt install mysql-Server
   sudo systemctl start mysql.service
   sudo mysql_secure_installation
   ```

2. **Create database and table**
   ```
   bash
   sudo mysql
   CREATE DATABASE bmi_db;
   CREATE TABLE `bmi_db`.`bmi`(`id` INT AUTO_INCREMENT NOT NULL, `name`VARCHAR(45) NOT NULL, `weight`DOUBLE NOT NULL,`bmi`DOUBLE NULL, PRIMARY KEY(`id`));
   ```
   
3. **Permit external connections to your db:**
	```
	bash
	CREATE USER 'dbuser'@'%' IDENTIFIED BY 'password';
	GRANT SELECT, INSERT, ALTER ON `bmi_db`.`bmi` TO 'dbuser'@'%';
	FLUSH PRIVILEGES;
	```
	
4. **Create external db user:**
	First, log into your Azure account and then access your Azure VM.
	Select "Network" in the navigation and add a new inbound port rule:
    Source: IP of your other server
    Destination: *
    Service: MySQL
    Action: Allow
    Priority: 310
    Name: MySQL   

5. **Change config on server:**
   ```
   bash 
   sudo nano /etc/mysql/mysql.conf.d/mysqld.cnf 
   ```
   Change the variables `bind-address` and `mysqlx-bin-address` from `127.0.0.1` to `0.0.0.0` allowing the mysql server to listen on all IPs.

   Restart MySQL service:

   `sudo systemctl restart mysql`

## Create a KeyVault and add secrets

Populate your secrets with the credentials of the created db user and the IP-adress of the Database-VM

## Springboot-Application Server

1. **Create another VM on Azure and install Java:**
   Download the latest version of java (https://www.oracle.com/de/java/technologies/downloads/) and use WinSCP (or SCP) to upload it onto your VM
   Alternatively you can use `apt install` to install the needed version of java

2. **Upload your Springboot-Application (jar-file built with Maven) to your VM:**
   `scp -i {private-key} {path to your Springboot-App.jar} {destination on VM e.g. azureuser@20.55.35.13:/home/azureuser/}`

3. **Run your app with:**
   `java -jar app.jar`