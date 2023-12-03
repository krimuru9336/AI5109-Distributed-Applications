
# AI5109-Distributed-Applications - Spring Boot Application

## Overview

This Spring Boot application, developed using the Spring Initializer with Maven, utilizes MySQL as its database. The primary purposes of this application 
1. **To calculate Body Mass Index (BMI)** 
2. **Provide weather data via https://api.openweathermap.org**


**The application will be accessible locally at localhost:8083.**

_Note_

Ensure that MySQL is properly configured and accessible before running the application.


## API Endpoints
#### **BMI Controller**

##### Calculate User BMI

`POST /bmi`

```
{
    "name": "John Doe",
    "height": 170,
    "weight": 65
}
```

##### Get User Information

`GET /user/{user-id}`

```
/user/1
```


#### **Weather Controller**

##### Get Weather Data

`GET /weather?lat={lat}&lon={lon}`

```
/weather?lat=10&lon=10
```


_Feel free to explore and use this Spring Boot application for BMI calculations and weather data retrieval!_











