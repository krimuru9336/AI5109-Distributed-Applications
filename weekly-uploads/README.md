#  😺 Cat BMI Calculation application

This is a Java Spring Boot application for Body Mass Index (BMI) calculations of cats. It can be used as one of many measures to check how in shape your cat is.

The BMI is calculated based on measurements taken at the circumference around the level of the 9th rib and the length of the lower back leg from knee to ankle. 
Further details can be found [here](https://worldanimalfoundation.org/cats/bmi-calculator/).

## 🚀 Architecure

The application follows a distributed architecture and is deployed on 3 seperate servers. The frontend is deployed as a Static Website via Azure Storage while the backend is deployed to an Azure Web App. The MySQL database is deployed to an Azure MySQL server.

## 📑 Frontend

The frontend consists of an index page with a form for calculating a cat's BMI along with a BMI list table, a JavaScript file to interact with a backend API for managing cat BMI data and a simple stylesheet.

## 📑 Backend

- BmiApplication - Main class serving as the entry point for the Spring Boot application. Initializes the Spring context and starts the server.

- BmiBean - Entity class for storing BMI information in the database. Represents BMI records with attributes such as name, gender, rib cage, leg length, and BMI.

- BmiController - Spring MVC controller for handling BMI-related requests. Manages endpoints for retrieving and saving BMI records, performs BMI calculations.

- BmiRepository - Spring Data JPA repository interface for CRUD operations on BmiBean entities. Provides methods for interacting with the database.

## 📑 Database

The database is a MySQL database.

## ⭐ Enhancements

As an added feature, a RESTful API consumer for retrieving cat facts from the [MeowFacts API](https://github.com/wh-iterabb-it/meowfacts) is included. 

- MeowFactsConsumer - Communicates with the external API and returns cat facts in the response.

- MeowFactsApiResponse - Java class representing the structure of the MeowFacts API response. Deserializes JSON responses into Java objects.

- RestTemplateConfig - Spring configuration class for defining a RestTemplate bean. Configures and provides instances of the RestTemplate class for making HTTP requests.