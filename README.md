# Personal Expense Tracker

The Personal Expense Tracker is a desktop budgeting application built for the CMSC 495 capstone. It combines a JavaFX user interface with a Spring Boot service layer and an embedded SQLite database so users can register, log in, manage expenses, organize transactions by category, search historical records, and view summary charts.

## Technology Stack

- Java 21
- Spring Boot 3.2
- JavaFX 21
- SQLite with `JdbcTemplate`
- Maven for build and dependency management
- JUnit 5, Mockito, TestFX, and JaCoCo for testing support

## Current Features

- User registration and login with normalized email handling
- Default categories plus user-defined custom categories
- Add, update, delete, and search expenses
- Total spending summary on the dashboard
- Pie chart and bar chart reporting by category
- Unit, integration, and UI test scaffolding

## Project Structure

- `src/main/java/com/expensetracker/model`
  Domain models for `User`, `Category`, and `Expense`
- `src/main/java/com/expensetracker/service`
  Application services for authentication, categories, and expenses
- `src/main/java/com/expensetracker/repository`
  Database bootstrapping and schema initialization
- `src/main/java/com/expensetracker/ui`
  JavaFX views and scene navigation
- `src/test/java/com/expensetracker`
  Unit, integration, repository, model, and UI tests
- `docs/Visuals`
  Mermaid diagrams used in capstone deliverables

## Running the Application

1. Install Java 21 and Maven 3.6.3 or newer.
2. Open a terminal in the project root.
3. Run `mvn clean install`.
4. Start the application with `mvn javafx:run` or `mvn spring-boot:run`.

The application stores data in a local SQLite file named `expense_tracker.db` in the `data/` directory.

## Testing

- `mvn test` runs the automated test suite
- JaCoCo is configured to produce coverage reports during test execution
- UI tests are present but currently disabled in headless environments due Java 21 and Monocle compatibility limitations

## Known Limitations

- The project is a local desktop application and does not yet support cloud synchronization
- Password storage uses a basic hash suitable for coursework, but a production version should use a stronger password-specific algorithm such as BCrypt or Argon2
- The application focuses on manual expense entry and does not yet support bank import, budgeting rules, or export workflows

## Documentation

- Environment requirements: [docs/environment_requirements.md](docs/environment_requirements.md)
- Architecture diagram: [docs/Visuals/deployment_architecture.mmd](docs/Visuals/deployment_architecture.mmd)
- Data model: [docs/Visuals/data_model.mmd](docs/Visuals/data_model.mmd)
- Login sequence: [docs/Visuals/login_sequence.mmd](docs/Visuals/login_sequence.mmd)
- Release guide: [docs/RELEASING.md](docs/RELEASING.md)
