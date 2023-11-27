#  😺 Cat BMI Calculation application

This is a Java Spring Boot application for Body Mass Index (BMI) calculations of cats. It can be used as one of many measures to check how in shape your cat is.

The BMI is calculated based on measurements taken at the circumference around the level of the 9th rib and the length of the lower back leg from knee to ankle. 
Further details can be found [here](https://worldanimalfoundation.org/cats/bmi-calculator/).

## 📑 Main modules

- BmiApplication - Main class serving as the entry point for the Spring Boot application. Initializes the Spring context and starts the server.

- BmiBean - Entity class for storing BMI information in the database. Represents BMI records with attributes such as name, gender, rib cage, leg length, and BMI.

- BmiController - Spring MVC controller for handling BMI-related requests. Manages endpoints for retrieving and saving BMI records, performs BMI calculations.

- BmiRepository - Spring Data JPA repository interface for CRUD operations on BmiBean entities. Provides methods for interacting with the database.

## ⭐ Enhancements

As an added feature, a RESTful API consumer for retrieving cat facts from the [MeowFacts API](https://github.com/wh-iterabb-it/meowfacts) is included. 

- MeowFactsConsumer - Communicates with the external API and returns cat facts in the response.

- MeowFactsApiResponse - Java class representing the structure of the MeowFacts API response. Deserializes JSON responses into Java objects.

- RestTemplateConfig - Spring configuration class for defining a RestTemplate bean. Configures and provides instances of the RestTemplate class for making HTTP requests.

## 🚀 Deployment

The application is deployed as an Azure App Service while the MySQL database is deployed to an Azure MySQL server.

## 🌐 Visit the website

Feel free to interact with the application by visiting https://cat-bmi.azurewebsites.net/ as well as enjoy interesting cat facts from https://cat-bmi.azurewebsites.net/meow-facts/meow.

## ✒️ Authors

- Sahan Wijesinghe
- Distributed Applications - AI5109 - English version (WiSe23/24)
- Hochschule Fulda

_This project is developed for educational and demonstration purposes only._