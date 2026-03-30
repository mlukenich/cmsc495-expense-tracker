# Project Plan: Cloud-Native Expense Tracker

## 1. Introduction

This Project Plan provides a formal roadmap for the development, implementation, and deployment of the **Cloud-Native Expense Tracker**. The primary purpose of this document is to establish a clear
architectural vision and operational framework for the project team, ensuring that all stakeholders have a shared understanding of the technical objectives, project milestones, and deliverables.

In today’s digital economy, personal financial management is a critical skill, yet many users struggle with fragmented tools or overly complex platforms. The **Cloud-Native Expense Tracker** addresses
this by providing a streamlined, intuitive web-based interface for logging, categorizing, and analyzing personal expenditures. However, the true significance of this project lies in its underlying
engineering.

Rather than building a traditional monolithic application, the project team has committed to an **enterprise-grade, microservices-based architecture**. By utilizing **React** for a responsive frontend
and **Spring Boot** for a decoupled backend, the team will demonstrate mastery of modern full-stack development. Furthermore, the project emphasizes "production-readiness" by incorporating:

*   **Containerization via Docker** for environment consistency and rapid deployment.
*   **Loosely Coupled Microservices** to ensure system resilience and independent scalability.
*   **Comprehensive Observability** through integrated monitoring tools like Prometheus and Grafana.

This project serves as a capstone achievement, demonstrating the team's ability to navigate the complexities of cloud-native development, automated testing, and professional DevOps practices while
delivering a functional, high-value product to the end user.

---

## 2. Statement of Work & Project Scope

The project team is tasked with the end-to-end design, development, and delivery of a modern web application for personal expense management. The project is divided into several high-level phases,
including architectural design, service implementation, frontend integration, and system validation.

### Project Goals and Objectives

*   **Responsive User Interface:** Develop a high-performance GUI using React and TypeScript that provides a seamless user experience across modern web browsers.
*   **Scalable Backend Services:** Implement a suite of independent Spring Boot microservices (Auth Service, Expense Core, Analytics Service) orchestrated by an API Gateway to ensure a modular and
    resilient backend.
*   **Continuous Deployment Pipeline:** Utilize Docker and Docker Compose to containerize the entire stack, ensuring that the application can be launched in any environment with a single command.
*   **System Integrity & Validation:** Maintain a high standard of code quality through a comprehensive testing suite, including unit tests, integration tests with Testcontainers, and frontend component
    verification.
*   **Real-Time Observability:** Integrate Spring Boot Actuator with Prometheus and Grafana to provide real-time dashboards for monitoring system health, API latency, and resource utilization.

### Project Deliverables

*   **Functional Web Application:** A fully deployed frontend and backend system accessible via a web browser.
*   **Infrastructure-as-Code (Local):** A `docker-compose.yml` configuration that manages the orchestration of all services, databases, and monitoring tools.
*   **Automated Testing Suite:** A collection of JUnit 5 and Vitest/Jest test cases that verify the correctness of the application’s business logic.
*   **Comprehensive Documentation:** Including this Project Plan, a detailed User Guide, and technical architecture diagrams.

### In-Scope Items

*   **User Management:** Secure user registration, profile management, and JWT-based authentication.
*   **Expense Management:** Full CRUD (Create, Read, Update, Delete) capabilities for individual expense transactions.
*   **Categorization & Budgeting:** Tools for organizing expenses into user-defined categories (e.g., Housing, Food, Transportation).
*   **Spending Analytics:** Visual reports summarizing spending habits, including monthly totals and category distribution charts.
*   **DevOps Infrastructure:** Full containerization of the stack and a functional monitoring/alerting dashboard.

### Out-of-Scope Items

*To maintain project focus and ensure timely delivery, the following features are excluded from this project phase:*

*   **Banking Integration:** Automated syncing with financial institutions via external APIs (e.g., Plaid).
*   **Mobile-Native Apps:** Development of standalone iOS or Android applications (the web UI will, however, be mobile-responsive).