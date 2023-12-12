# Distributed Application Lecture

## Application

During the first 4 Exercises I have created an application for contacts. With a form the user can insert new contacts containing name and phonenumber. All contacts inside the database are shown in an table below the form.

Besides the contacts application I have also included another application called random dog. When calling <Server IP>/randomDog I will call the free random dog API to recieve an random picture of a dog. The site will show a link to open the picture in a different tab, the acutall response of the API, the status code and all the headers for the request

## Setup

### Used Servers

Spring Boot Application running on Azure Virtual Machine (using Azure Student Progamm)
MySQL Database running on Azure Database for MySQL Servers (using Azure Student Programm)

### Used technology on VM

- Java 17
- Nginx 1.18.0
- Gradle 8.1

## Tutorial Starting Application

1. Start VM and DB on portal.azure
2. Connect to VM using ssh and private keys stored on Mac and PC
3. Start Spring Boot Application
```bash
java -jar /build/libs/distributedapplications1-0.0.1-SNAPSHOT.jar
```
4. Pray that the application starts without errors
5. Visit <Public IP> to see contacts application and <Public IP>/randomDog to see random dog application
6. After done testing the application remember to stop VM and DB to not waste student credit
