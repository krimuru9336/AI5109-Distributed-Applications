# Distributed Applications - Exercise 'Deployment and GitHub setup'

My application consists of a single Java Spring Boot application (frontend and backend). It contains a _docker-compose.yaml_ file to run a database and a phpmyadmin installation for viewing the database data.

The following installation is to host the whole application on two virtual machines. One for the database and one for the application. You need to install the following software:

__Database server__
- mysql-server

__Application server__
- openjdk-17-jdk
- maven

Now copy the whole sourcecode (e.g. via git) onto the server. Set the correct database credentials under _src/main/java/de/lorenz/basic_spring_setup_ and run the following command to build the jar file:
```
mvn clean install
```

Start the application via the following command:
```
java -jar target/basic-springboot-0.0.1-SNAPSHOT.jar
```

## Start application locally
- `docker compose up`
- Start the _Application.java_