# BMI Springboot Application

## About The Project

Welcome to my BMI Calculator Basic Spring Boot Application! 
In addition to the core functionality, this application has been extended to provide a delightful feature - the ability to display random adorable duck pictures on the frontend. These duck pictures are fetched via the random-d.uk API, adding a fun and quirky touch to your BMI calculation experience.

This comprehensive guide will help you understand and navigate through the source code and using the application,  featuring duck pictures. 
The application itself is built on the Spring Boot framework with an HTML frontend powered by Thymeleaf. The application captures user names, weight, and height through a HTML form. After a click on the submit button this information is stored  in the backend using a MySQL database. To accomplish this, the application leverages Spring Boot's controller functionality, Java Persistence API (JPA) for database interaction, and a dedicated Java Bean named "BmiBean."

Whether you're a developer interested in exploring the inner workings of this absolutely complex application or a user seeking an enjoyable way to calculate your BMI with the company of duck pictures, this readme will provide you with the necessary information to get started and make the most of this BMI Calculator Spring Boot Application. Let's dive in!

## Getting Started

To get started, just clone the repo and start developing.
You just have to set up your SQL database right and enter the data source URL, username and password to the application.properties file.

## Components
The application is split into different classes.
### BmiApplication.java
Just the main method. Nothing special here.
### BmiBean.java
*POJO / Bean (Container for data from input) for your BMI data.*

The `BmiBean` is a Plain Old Java Object (POJO) designed to represent and store essential data related to a person's Body Mass Index (BMI). This class serves as the backbone for managing BMI information within our Spring Boot application. Here's a brief overview of its key attributes and purpose:

- **Table Representation:** This entity is mapped to a database table named "bmi" using JPA annotations.

- **Attributes:**
  - `name`: Represents the name of the individual for whom BMI data is recorded.
  - `weight`: Stores the weight of the person in kilograms.
  - `height`: Records the height of the person in meters.

- **Calculated BMI:** The `getBmi()` method calculates the BMI based on the stored weight and height data. The BMI, a common health indicator, is computed as the weight in kilograms divided by the square of the height in meters.

This `BmiBean` is an integral part of our application, facilitating the storage and retrieval of BMI-related information, allowing users to track their health and fitness progress effectively.

### BmiBeanRepository.java

The `BmiBeanRepository` interface serves as a crucial component in our Spring Boot application, providing a link between the application and the database. Here's a brief description of its purpose and functionality:

- **Data Repository:** This interface extends the `JpaRepository` from Spring Data JPA, which simplifies database interactions and provides common CRUD (Create, Read, Update, Delete) operations for our `BmiBean` entity.

- **Link to "bmi" Table:** It establishes a connection to the "bmi" table in the database, allowing seamless storage and retrieval of BMI data records.

- **Inherited Methods:** By extending the `JpaRepository`, this interface inherits methods for tasks such as saving, querying, and deleting `BmiBean` entities, making it effortless to manage and interact with BMI data.

In essence, the `BmiBeanRepository` interface acts as a bridge between our Spring Boot application and the database, enabling efficient data management and retrieval of Body Mass Index (BMI) records. This interface streamlines database operations, ensuring a smooth user experience for tracking and analyzing BMI-related information.

### BmiController.java

The `BmiController` is the core component of our Spring Boot application, responsible for managing the BMI-related functionality and integrating the delightful feature of displaying random duck pictures on the frontend. Here's a concise overview of its key responsibilities and features:

- **BMI Data Management:** Manages the creation, storage, and retrieval of BMI data using the `BmiBeanRepository`, allowing users to track their health and fitness progress.

- **Duck Picture Integration:** Incorporates the capability to fetch and display random duck pictures on the application's frontend via an external API call. This adds an enjoyable touch to the user experience.

- **URL Mapping:** Maps various URL endpoints to corresponding controller methods, facilitating user interactions with the application.

- **Dependency Injection:** Utilizes Spring's dependency injection to access the `BmiBeanRepository` and configuration properties, enhancing modularity and maintainability.

In summary, the `BmiController` is at the heart of our application, offering both essential BMI data functionality and a lighthearted twist with random duck pictures, creating an engaging and practical user experience.


### DuckApiResponse.java

The `DuckApiResponse` class is a model used to represent the response received from an external Duck Picture API. This response typically contains information about a duck picture, and the class has two main attributes:

- **`message`:** A property that may contain additional information or a message from the API, although it is often empty for image responses.

- **`url`:** The URL pointing to the location of the duck picture retrieved from the API.

This class is essential for deserializing the API response into a usable format, allowing our application to extract and display duck pictures on the frontend. It serves as a bridge between the external API's response and our application's frontend, enhancing user engagement and enjoyment.

### index.html

This HTML template represents the user interface for the BMI Calculator Spring Boot application. It serves as the frontend of the application and is responsible for rendering user input forms and displaying BMI data, along with the delightful feature of random duck pictures. Here's a concise description of its key components:

- **Metadata:** The document starts with essential metadata, including the document type declaration and character encoding, ensuring proper rendering in a web browser.

- **Header:** Provides a title and includes optional authorship and creation date information for reference.

- **Body Content:**
  - **Heading:** Displays a simple greeting in an `<h1>` element, welcoming the user.
  - **Duck Picture:** Utilizes Thymeleaf to dynamically set the image source (`th:src`) to the URL provided by the application's controller. This allows the display of a random duck picture with an associated height and alt text.
  - **BMI Input Form:** Offers a form where users can input their name, height (in meters), and weight (in kilograms). Upon submission, this data is sent to the backend for BMI calculation and storage.
  - **BMI Data Table:** Presents a table that lists previously recorded BMI data. The table is populated with information fetched from the backend, with columns for name, height, weight, and BMI.

This HTML template is an integral part of the application, offering a user-friendly and interactive interface for users to input, visualize, and manage their BMI data, all while enjoying the occasional appearance of charming duck pictures.
