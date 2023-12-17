# Module 1

The application uses Kotlin and Jetpack Compose. In order for the application to work correctly, it must be connected to the wormhole API, which in turn must be connected to a MySQL database. Use Android Studio to build the app.

To implement this, the following files must be adapted and created:

- `ChitChat\app\src\main\java\com\example\chitchat\MainActivity.kt`
- `wormhole\.env`

## wormhole API

The wormhole API can be started as follows:

```bash
docker compose up -d
```

An .env file must be provided in the same folder as the Docker Compose file containing the following entries:

```env
DA_DATABASE_USERNAME=<username>
DA_DATABASE_PASSWORD=<password>
DA_DATABASE_HOST=<address>
DA_DATABASE_NAME=<database>
```


## Database
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
CREATE DATABASE <database>;
```

Store the access data in an **.env**-file in the root directory of the module:

```env
DA_DATABASE_USERNAME=<username>
DA_DATABASE_PASSWORD=<password>
DA_DATABASE_HOST=<address>
DA_DATABASE_NAME=<database>
```

