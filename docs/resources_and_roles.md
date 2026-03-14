 ## 3. Resource List & Team Member Roles
    
    In order to successfully develop, test, and deploy the Cloud-Native Expense Tracker, the team will leverage a combination of hardware, software, and infrastructure resources. These resources are selected
    to ensure a modern, professional development environment and high-quality deliverables.
    
    ### 3.1 Resource List
    
    #### Hardware Resources
    *   **Development Machines:** Personal Laptops/Workstations with a minimum of 16GB RAM to support running multiple Docker containers (Microservices, Databases, Monitoring) simultaneously.
    *   **Version Control Hosting:** GitHub will be used as the central repository for code management, collaboration, and issue tracking.
   
   #### Software & Frameworks
   *   **Backend Framework:** Java 17+ with Spring Boot 3.x.
   *   **Frontend Framework:** React 18+ with TypeScript and Vite.
   *   **Database:** PostgreSQL 15+ (Relational Database Management System).
   *   **Integrated Development Environments (IDEs):** IntelliJ IDEA (Backend), VS Code (Frontend), Antigravity (IDE).
        
   #### Infrastructure & DevOps Tools
   *   **Containerization:** Docker Desktop and Docker Compose.
   *   **Testing:** JUnit 5, Mockito, Testcontainers (Backend); Vitest or Jest (Frontend).
   *   **Monitoring & Observability:** Spring Boot Actuator, Prometheus, and Grafana.
   *   **API Documentation:** SpringDoc OpenAPI (Swagger UI) for automated API documentation.
   
   ### 3.2 Team Member Roles
   
   To ensure accountability and a smooth workflow, the following roles have been assigned. Each member is responsible for the design, implementation, and testing of their respective domain.
   
   | Role | Responsibility Description |
   | :--- | :--- |
   | **Project Manager** | Responsible for overall project coordination, ensuring milestones are met, and maintaining this Project Plan. Acts as the primary point of contact for status updates and manages the Risk Management strategy. |
   | **Backend Engineer** | Leads the design and implementation of the Spring Boot microservices, including the API Gateway and Auth Service. Responsible for database schema design and API security. |
   | **Frontend Engineer** | Responsible for the React/TypeScript architecture. Designs and implements the user interface, ensures responsive design, and integrates the UI with backend REST APIs. |
   | **DevOps & QA Engineer** | Owns the containerization strategy (Docker) and the monitoring stack (Grafana/Prometheus). Responsible for the overall testing strategy, ensuring high code coverage and system reliability. |