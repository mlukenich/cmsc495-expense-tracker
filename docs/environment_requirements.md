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

---

## Docker

Docker containerizes the application so no local JDK, Maven, or JavaFX installation is required. The JavaFX UI is rendered inside the container using a virtual display and streamed to your browser via noVNC (https://novnc.com/).

### Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (Windows / macOS) or Docker Engine (Linux)

---

### Running the application

**1. Build the image and start the container:**

```bash
docker compose up --build
```

**2. Open the application in your browser:**

```
http://localhost:6080/vnc.html
```

The application window will appear in your browser tab. This works the same way on Windows, macOS, and Linux — no additional setup required.

**3. Stop the container:**

```bash
docker compose down
```

---

### How it works

The container runs three background services before launching the Java application:

| Service                | Role                                                                                    |
| ---------------------- | --------------------------------------------------------------------------------------- |
| **Xvfb**               | Virtual X11 framebuffer — gives JavaFX a display to render to without a physical screen |
| **x11vnc**             | Exposes the virtual display as a VNC stream on `localhost:5900` (container-internal)    |
| **noVNC + websockify** | Proxies the VNC stream over WebSocket and serves the HTML5 client at port 6080          |

---

### Data persistence

The SQLite database is stored in a Docker named volume (`expense-data`) mounted at `/data` inside the container. The data survives container restarts and `docker compose down`.

```bash
# Inspect volume location on the host
docker volume inspect cmsc495-expense-tracker_expense-data

# Stop containers but keep the volume
docker compose down

# Stop containers AND delete the volume (all data lost)
docker compose down -v
```

### Useful Docker commands

```bash
# Rebuild image after source changes
docker compose up --build

# Run in the background (browser UI still available at localhost:6080)
docker compose up -d

# View logs
docker compose logs -f

# Stop and remove containers
docker compose down
```
