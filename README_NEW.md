#Basic Spring boot application

##Contents:
1. Database
2. Backend Springboot Application
3. Frontend

##Application goal:
User should be able to interact with a form in frontend.
The form has two input fields for entering Name and Phone number.
Upon clicking submit - User entered details are added to database in real-time and the Frontend displays all existing users.

Conditions - User needs to enter both name and number to be able to submit.


##Database Structure:
MySQL Workbench and Server

Installation - Follow the wizard for installation of workbench and server.
Set password for user root.

To create a table - Create from workbench with following config-
Table: user
Columns:
id int AI PK 
number varchar(255) 
name varchar(255)

Making ID field as primary key, setting up auto increment on  it.


##Application backend:
Dependencies-
Spring Web
Spring Data JPA
Thymeleaf
H2 Database
MySQL Driver
Spring Data JDBC
 
Spring initializr - https://start.spring.io/
Backend template downloadedfrom above link with the dependencies listed above
1. Open 'src/main/resources/application.properties' and add details of mysql database, username, password
2. Java files- 
   controller.java  - a java class for handling GET, POST HTTP requests to interact with frontend
   User.java  -  a java class that defines user database table fields and getters and setters for each field.
   UserRepository.java  - Interface - provides methods to interact with database. Also called bean in Spring.
3. In resources folder there are html and js files 
   index.html - the page that user interacts with. 
                Has a form with two fields for name and phone number.
                User can click on submit button only upon filling both the fields.
                Once clicked on submit - The table below refreshes with data from database.
4. Once all coding is complete - Build the spring application. It starts running on port 8080 (default), to access the webpage - hit on http://localhost:8080/index on browser tab.



##APIs:
The application uses an external API to confirm whether user has entered a valid phone number or not.
If invalid - User is prompted to enter a correct one and only a correct number is fed to database.