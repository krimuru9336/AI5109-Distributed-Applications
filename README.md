Student Data


Client:
A simple application that takes the Name and Phone number and stores it in the database.

Steps to run this project:

npm i
npm run dev
That's it!

Server:

Steps to run this project:

npm i
cd src
node index.js
That's it and your server should start.

You will require the below databse and table in mysql:

Run the below queries in mysql:

CREATE DATABASE lab_da /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci / /!80016 DEFAULT ENCRYPTION='N' */;

CREATE TABLE ex1 ( id int NOT NULL AUTO_INCREMENT, name varchar(45) NOT NULL, phone varchar(45) NOT NULL, PRIMARY KEY (id), UNIQUE KEY id_UNIQUE (id) ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;