# Environment Requirements

This document outlines the software requirements and configuration needed to build and run the CMSC495 Expense Tracker application.

## Prerequisites

Ensure the following tools and software are installed on your development machine before attempting to build or run the application:

### 1. Java Development Kit (JDK)
- **Version**: Java 21
- **Notes**: The project is configured to use Java 21. Ensure you have JDK 21 installed and your `JAVA_HOME` environment variable is pointing to the JDK 21 installation directory.

### 2. Apache Maven
- **Version**: 3.6.3 or higher (Recommended)
- **Notes**: Used for dependency management and building the project. Ensure `mvn` is accessible from your system's command line or terminal.

### 3. JavaFX
- **Version**: JavaFX 21.0.2
- **Notes**: The application uses JavaFX for its graphical user interface. The Maven `pom.xml` is configured to manage JavaFX dependencies automatically (`javafx-controls`, `javafx-fxml`), so you do not need a standalone SDK downloaded if you are running or building via Maven.

### 4. Database
- **Type**: SQLite
- **Notes**: The application utilizes an embedded SQLite database. The driver (`sqlite-jdbc` version 3.45.1.0) is automatically fetched by Maven. No standalone database server installation or configuration is required.

### 5. Python (Optional Scripts)
- **Version**: Python 3.x
- **Notes**: Based on the project structure, some auxiliary scripts may use Python. Ensure Python 3 is installed if you need to execute project administration scripts.

### 6. Operating System
- **Supported OS**: Windows, macOS, Linux
- **Notes**: The application is cross-platform. Ensure that your environment supports rendering JavaFX UI.

---

## Building the Application

To build the application, resolve all necessary dependencies, and run tests, navigate to the project's root directory (where `pom.xml` is located) and run:

```bash
mvn clean install
```
This process will create the packaged `.jar` file inside the target directory.

## Running the Application

Since this is a Spring Boot application, it can be run seamlessly from the command line using the Spring Boot Maven plugin:

```bash
mvn spring-boot:run
```

Alternatively, you can run the compiled package directly (if it has been successfully built previously):

```bash
java -jar target/cmsc495-expense-tracker-1.0-SNAPSHOT.jar
```
