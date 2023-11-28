# Readme FDAI5588
# 1 General
- Git Repo: https://github.com/krimuru9336/AI5109-Distributed-Applications
- Author: Adrianus Jonathan Engelbracht
* Matrikelnummer: 1151826
# 2 Application
- The application is used to calculate the BMI when entering weight, height and name and give the output to the user. 
# 3 Technologies
## 3.1 Backend
- Springboot
	- Calculates the bmi
	- communicates with the database (storing data) and the frontend (receiving input, returning output)
## 3.2 Frontend 
- *is included in the springboot application*
- Thymeleaf
	- is used to display the form and the output to the user. 
## 3.3 Database
- Microsoft SQL Server
# 4 Setup 
## 4.1 Database Server
### 4.1.1 Information
- Servername: distributedappserver.database.windows.net
- Databasename: bmidatabase
- Port: 1433
### 4.1.2 Azure Configuration
- Whitelist the public ip adress of the remote application server and the development computer
  - SQL Server -> Networking -> Public Access
    - Public Network Access -> Selected Networks
    - Firewall Rules
      - Add rule 
## 4.2 Application Server
- hosts springboot application including thymeleaf templates (backend+frontend)
### 4.2.1 Information
- Serveradress: 20.163.60.62
- Application Port: 8081 (needs to be enabled by firewall)
# 5 Deployment
- Build Jar file of the project
```bash
mvn clean install
```
- Copy the file to the remote server using scp
- Start the springboot application
```bash
java -jar <applicationName>.jar
```
# 6 Testing
- Access the application http://20.163.60.62:8081/