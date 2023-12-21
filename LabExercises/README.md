# Distributed Applications Lab Exercises By Mohammed Amine Malloul

## Description
This Spring Boot application is part of my learning in the module Distributed Applications. It was created as part of lab exercises to set up a basic application with frontend, backend, and database components.

## Table of Contents
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)


## Features
- Input form with fields for name and phone number, and a submit button
- Display of user data from the database in a div
- Storing user-submitted data in the database and displaying it on the page
- Integration with an external API to fetch gold prices in JSON format

## Prerequisites
Ensure that you have the following prerequisites installed on your machine:
- Java Development Kit (JDK) 17
- Apache Maven 3.6.3
- MySQL Workbench or similar
- Mysql 8.*
- A database called basic already created in Mysql server

## Installation
1. Clone the repository: `git@github.com:Aminemalloul/bmi.git`
2. Navigate to the project directory: `cd bmi`
3. Build the application: `mvn clean install`
4. Run the application: `java -jar target/BMI.jar`

## Usage

### Lab 1: Basic Spring Boot Setup
To access the default endpoint, navigate to the root URL: http://localhost:8080/

This will display a simple form with input fields for name and phone number, as well as a submit button. Upon submission, the entered data is stored in the database, and the contents are displayed on the same page.

### Lab 2: Calling APIs
To fetch gold prices, use the following endpoint: http://localhost:8080/fetch

This endpoint calls an external API that provides gold prices in JSON format.

.
