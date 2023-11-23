# Installation
The Database and springboot application installations were done in azure
and docker. 

Every VM is configured identical:
* update
* upgrade
* install docker

## Database
The MySQL server can be installed/started with a single 

``docker compose up``

and will run indefinitely, even when the container stops or the vm is restarted.
Everything is configured in the docker-compose.yml.
```
version: '3.8'

services:
mysql-server:
image: mysql
container_name: mysql-server
restart: always
environment:
MYSQL_ROOT_PASSWORD: ${DATABASE_PASSWORD}
MYSQL_DATABASE: week_one
ports:
- "3306:3306"
volumes:
- mysql_data:/var/lib/mysql

volumes:
mysql_data:
```
This will start a container with the mysql-server running locally on the vm. 
To access the DB externally we need to open the traffic for the port 3306 in azure (inbound and outbound).
After that the DB is accessible from the local network and external networks.

## Springboot
The installation for the springboot application was done identically to the database, except the different docker-compose.yml and a scp command.
The scp command is for uploading our jar that is build from our project. It contains the application with all dependencies.

``scp -i ${PATH_TO_SECRET} ${SOURCE_FILE_JAR} azureuser@${IP}:./``

After the file is copied to the server, we need to move it in the same directory as our docker-compose file and start the container.
```
services:
  springbootapp:
    container_name: springboot-app
    image: openjdk:19
    volumes:
      - ./application.jar:/app/application.jar
    ports:
      - "8080:8080"
    environment:
      - DATABASE_HOST=${DATABASE_HOST}
      - DATABASE_PASSWORD=${DATABASE_PASSWORD}
    command: java -jar /app/application.jar
    restart: always
```
Now the vm will always run the springboot application on the default port 8080.
The last step is to make it accessible outside the local network. 
For that I use the NGINX as a reverse-proxy to redirect all http calls on the vm to localhost:8080 and back.

``sudo apt install nginx``

After installing NGINX we change the default configuration for the correct redirection.

The *location* point needs to be changed in the ``/etc/nginx/sites-available/default`` file.

```
[...]
location / {
        # First attempt to serve request as file, then
        # as directory, then fall back to displaying a 404.
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
}
[...]
```
After that the springboot application is running and can be accessed by HTTP from the internet over the public ip of the vm.