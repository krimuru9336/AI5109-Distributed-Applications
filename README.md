# AI5109-Distributed-Applications

This README describes what I did to solve Exercise 3
The Springboot-App uses the KeyVault we created in Azure

## Database Server

1. **Create a VM on Azure and install/setup Mysql:**
   ```bash
   sudo apt update
   sudo apt install mysql-server
   sudo systemctl start mysql.service
   sudo mysql_secure_installation
   ```

2. **Create database and table:**
   ```bash
   sudo mysql

   CREATE DATABASE bmi_db;

   CREATE TABLE `bmi_db`.`bmi`(`id` INT AUTO_INCREMENT NOT NULL, `name` VARCHAR(45) NOT NULL, `weight` DOUBLE NOT NULL, `height` DOUBLE NOT NULL, `bmi` DOUBLE NULL, PRIMARY KEY (`id`));
   ```

3. **Allow external connections to your db:**
   ```bash
   CREATE USER 'dbuser'@'%' IDENTIFIED BY 'password';

   GRANT SELECT, INSERT, ALTER ON `bmi_db`.`bmi` TO 'dbuser'@'%';

   FLUSH PRIVILEGES;
   ```

4. **Create external db user:**
   Log into azure, access your VM and click on "Network" in the navigation.

   Add a new inbound port rule:

   Source: IP of your other server

   Destination: *

   Service: MySQL

   Action: Allow

   Priority: 310

   Name: MySQL   

5. **Change config on server:**
   ```bash 
   sudo nano /etc/mysql/mysql.conf.d/mysqld.cnf 
   ```
   Change `bind-address` and `mysqlx-bin-address` from `127.0.0.1` to `0.0.0.0` allowing the mysql server to listen on all ips.

   Restart MySQL Service:

   `sudo systemctl restart mysql`

## Create a KeyVault and fill it with secrets

Populate your secrets with the credentials of the created db user and the IP-Adress of the Database-VM

## Springboot-Application Server

1. **Create another VM on Azure and install Java:**
   For example you can download the latest version of java (https://www.oracle.com/de/java/technologies/downloads/) and use SCP to upload it onto your VM
   Alternatively you can use `apt install` to install the needed version of java

2. **Upload your Springboot-Application (jar-file built with Maven) to your VM:**
   `scp -i {private-key} {path to your Springboot-App.jar} {destination on VM e.g. azureuser@20.55.35.13:/home/azureuser/}`

3. **Now you can run your app with:**
   `java -jar app.jar`