
# Distributed Applications - WS 2023/2024
This repository contains source code for the "Distributed Applications" module in the Winter Semester 2023/2024 at Hochschule Fulda.

## Runtime Environment
The frontend, backend and the database are deployed on three virtual machines. Ubuntu is used as the OS. Docker as the CRE.

## VPN Connection
Before proceeding, ensure you have a WireGuard VPN connection. The virtual machines can only be accessed via a connection to the associated WireGuard VPN.

## Deploy Docker on the VM
To set up the Docker on the VM, run the following commands:

```bash
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
sudo apt-get update
sudo apt install -y docker-ce docker-ce-cli containerd.io
sudo groupadd docker
sudo usermod -aG docker $USER
```

## Database Deployment
To set up the database in Docker, run the following command:

```bash
docker run -p 3306:3306 --name docker-mysql -e MYSQL_ROOT_PASSWORD=<root_password> -d mysql:8.0
```

Create a reboot cronjob:

```bash
echo "@reboot docker start docker-mysql" | sudo tee -a /etc/crontab
```

Access the database with **MySQL Workbench** and set it up:

```sql
CREATE DATABASE <name_of_database>;
```

Store the access data in an **.env**-file in the root directory of the module:

```env
DA_DATABASE_URL=jdbc:mysql://{address_of_database}:{port_of_database}/{name_of_database}
DA_DATABASE_USERNAME={username}
DA_DATABASE_PASSWORD={user_password}
```

This file will later be used by Docker to pass the secrets into the backend.

## Backend Deployment
To deploy the backend, you need **Maven**, **Docker** and **Make**.

1. Execute the Maven build of the module.

2. Execute **make deploy** in the module.
   ```bash
   make deploy
   ```

3. Follow the instructions.

4. Create a reboot cronjob:
   ```bash
   echo "@reboot docker start da-spring-app" | sudo tee -a /etc/crontab
   ```

## Frontend Deployment
To deploy the frontend, you need **Docker** and **Make**.

1. Execute **make deploy** in the frontend folder.
   ```bash
   make deploy
   ```

2. Follow the instructions.

3. Create a reboot cronjob:
   ```bash
   echo "@reboot docker start frontend-nginx-container" | sudo tee -a /etc/crontab
   ```

## Dependency Installation (Windows)
- To use Docker, please install and start [Docker Desktop](https://www.docker.com/products/docker-desktop/).
- GNU Make can be installed under Windows with `choco install make`.
- It is assumed that you are using IntelliJ IDEA.

## Issues with the Project
If IntelliJ IDEA can't find your class, refer to [this Stack Overflow thread](https://stackoverflow.com/questions/47795758/intellij-run-configuration-cant-find-spring-boot-class) for troubleshooting.
