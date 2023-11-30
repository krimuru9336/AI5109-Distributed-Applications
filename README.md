# AI5109-Distributed-Applications

This README outlines the guidelines for working with this repository. It is essential to follow these guidelines to maintain a structured and collaborative development process.

## Basic Principles

1. **No Direct Work on Main Branch:**
   - No one should directly work on the `main` branch.

2. **Individual Project Branches:**
   - Each contributor manages their work on a branch named after their FD number (e.g., `fdai1234`).

3. **Prohibition of Editing Others' Branches:**
   - Editing or making changes directly to branches other than your own is strictly prohibited.

## Working on Your Own Branch

Follow these steps to create and work on your own branch:

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/krimuru9336/AI5109-Distributed-Applications.git
   cd AI5109-Distributed-Applications
   ```

2. **Create a New Branch Named After Your FD Number:**
   ```bash
   git checkout -b fdai1234
   ```
   - Replace `fdai1234` with your actual FD number.

3. **Make Changes:**
   - Implement your features or make necessary changes.

4. **Commit Changes:**
   ```bash
   git add .
   git commit -m "Your meaningful commit message here"
   ```

5. **Push Changes to Your Branch:**
   ```bash
   git push origin fdai1234
   ```
   - Replace `fdai1234` with your actual FD number.
  
## Spring Boot CRUD Application

This is a simple CRUD (Create, Read, Update, Delete) application built using Spring Boot. The application allows users to perform basic CRUD operations on entities, demonstrating the use of Spring Boot for developing robust and scalable web applications.
## Dependencies used:
Mysql server
thymleaf
sprinweb
spring jdbc

## Prerequisites

Before you begin, ensure you have met the following requirements:

- Java Development Kit (JDK) installed (version 8 or later)
- Maven build tool installed
- Git installed (optional for cloning the repository)

## Run the application
java -jar target/crudapplication-0.0.1-SNAPSHOT.jar

