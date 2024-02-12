# Week 4-5

## Objectives:

Separate backend, database and frontend, deploy at separate locations and establish connections.

### Backend

-   Separated backend java code,
-   Added CORS bean to allow required origin URL (frontend static website) ,
-   Enable https
    1.  Generate keystore

``` bash
keytool -genkey -alias myapp -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650
```

2.  Add to application.properties-

    ``` bash
    server.port=8443
    server.ssl.key-store=classpath:keystore.p12
    server.ssl.key-store-password=your_password
    server.ssl.keyStoreType=PKCS12
    server.ssl.keyAlias=myapp
    ```

-   Created jar file -

``` bash
//in Intellij , Go to maven - project name - package
mvn clean install -DskipTests
```

-   Deployed to Azure VM.

Steps-

1.  **Create** new VM in azure - <https://portal.azure.com/>
2.  From local machine, **Connect** via ssh command-

``` bash
   ssh azureuser@<backend_vm_ip>
```

It prompts to enter password, after which VM is connected.

OR

Use an ssh key while creating VM to connect with below command-

``` bash
ssh -i <key_file_name>.pem azureuser@<backend_vm_ip>
```

3.  Install java and then Transfer jar file to a folder (named backend) on VM by using the following command from local machine:

    ``` bash
    scp -r  Spring_Week1-0.0.1-SNAPSHOT.jar azureuser@<backend_vm_ip>:/home/azureuser/backend
    ```

4.  To run jar file in VM-

    ``` bash
    java -jar Spring_Week1-0.0.1-SNAPSHOT.jar
    ```

Permissions at Azure side-

Settings -\> Networking -\> Allow https connectivity at port 8443.

### Database

Separated database and deployed to Azure VM.

Install mysql dependencies on the VM. Create user, schema , table and add bind-address = 0.0.0.0 in mysqld.cnf file.

``` bash
CREATE USER 'myuser'@'<backend_vm_ip>' IDENTIFIED BY '<password>'; 
GRANT ALL ON . TO 'myuser'@'<backend_vm_ip>'; 
FLUSH PRIVILEGES;
```

To start mysql-

``` mysql
sudo systemctl start mysql
```

Permissions at Azure side-

Settings -\> Networking -\> Allow MySQL service connectivity to port 3306 for backend vm ip.

### Frontend

Separated Frontend files - index.html, index.js from backend code.

Create a new **Storage Account** in Azure and **Enable Static website.** It creates a container named **\$Web** -place frontend files in it.

Setup CORS and allow required origin URLs