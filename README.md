# AI5109-Distributed-Applications - Spring Boot Application

## Overview

This Spring Boot application, developed using the Spring Initializer with Maven, utilizes MySQL as its database.
The purpose of this application

1. **Calculate BMI (Body Mass Index)**
2. **Provide Apple music data via https://itunes.apple.com**

## Local Setup

### Clone Repository

```bash
git clone https://github.com/gihanHsFulda/DistributedApp.git
cd DistributedApp
```

**The application will be accessible locally at localhost:8080.**

_Note_

Ensure that MySQL is properly configured and accessible before running the application.

## API Endpoints

#### **BMI Controller**

##### Calculate BMI

`POST /calculate`

```
{
    "name": "John Doe",
    "height": 180,
    "weight": 70
}
```

##### Get All Users

`GET /users`

```
/users
```

#### **Music Controller**

##### Get Music Data

`GET /music?artist={artist name}`

```
/music?artist=edsheeran
```

## Production Deployment

The production version of the application is deployed on Azure and publicly accessible at https://bmiappbe.azurewebsites.net/
