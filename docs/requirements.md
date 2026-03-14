## 4. Requirements Analysis

This section details the specific requirements for the Cloud-Native Expense Tracker. These requirements are categorized into Business, Functional, and Technical domains to provide a comprehensive view of
the system’s design and operational constraints.

### 4.1 Business Requirements (BR)
Business requirements define the high-level goals and the value proposition of the application from the perspective of the user and the development organization.

*   **BR.1: User Financial Empowerment:** The system shall provide users with a centralized platform to gain a clear understanding of their spending habits, promoting better financial decision-making.
*   **BR.2: Zero-Cost Infrastructure (OSS):** The project shall utilize Open Source Software (OSS) to ensure the solution is cost-effective to develop and deploy, requiring no proprietary licensing fees.
*   **BR.3: Data Integrity and Reliability:** The application must ensure that financial records are stored accurately and persist through system restarts or service failures, maintaining a high level of
    user trust.
*   **BR.4: Professional Portfolio Development:** The project must demonstrate advanced software engineering principles (microservices, containerization, observability) to serve as a high-value capstone
    for the team’s academic and professional advancement.

### 4.2 Functional Requirements (FR)
Functional requirements define the specific behaviors and features the system must support.

#### 4.2.1 User Authentication and Security
*   **FR.1.1: User Registration:** The system shall allow new users to create an account using a unique email address and a secure password.
*   **FR.1.2: Secure Login:** The system shall authenticate users and issue a stateless JSON Web Token (JWT) for secure session management.
*   **FR.1.3: Account Persistence:** Users shall be able to log out, and their session should be securely terminated on the client side.

#### 4.2.2 Expense Management (CRUD)
*   **FR.2.1: Expense Creation:** Users shall be able to record an expense by providing an amount, date, category, and an optional description.
*   **FR.2.2: Expense Retrieval:** Users shall be able to view a chronological list of their transactions.
*   **FR.2.3: Expense Modification:** Users shall be able to edit existing expense records to correct errors or update information.
*   **FR.2.4: Expense Deletion:** Users shall be able to permanently remove an expense record from their history.

#### 4.2.3 Categorization and Analytics
*   **FR.3.1: Category Management:** The system shall provide predefined categories (e.g., Food, Rent, Entertainment) and allow users to create custom categories.
*   **FR.3.2: Monthly Summaries:** The system shall calculate and display the total spending for the current calendar month.
*   **FR.3.3: Data Visualization:** The system shall generate a visual breakdown (e.g., a pie or bar chart) showing the distribution of spending across different categories.

#### 4.2.4 System Operations and Monitoring
*   **FR.4.1: Health Check Endpoints:** Each microservice shall provide an `/actuator/health` endpoint to report its current operational status.
*   **FR.4.2: Real-Time Monitoring:** The system shall provide a dashboard (Grafana) that visualizes active user sessions, API request volumes, and error rates.

### 4.3 Technical Requirements (TR)
Technical requirements define the architectural constraints, performance standards, and security protocols.

#### 4.3.1 Architectural Constraints
*   **TR.1.1: Microservices Architecture:** The backend must be composed of at least three distinct services (API Gateway, Auth Service, Core Service) to ensure loose coupling.
*   **TR.1.2: Containerization:** Every component, including the frontend and the database, must be containerized using Docker to ensure environment parity.
*   **TR.1.3: Database Schema:** The system must utilize a relational PostgreSQL database with normalized tables to ensure data consistency and referential integrity.

#### 4.3.2 Performance and Scalability
*   **TR.2.1: API Latency:** The system should process 95% of standard CRUD requests in under 200ms under normal load conditions.
*   **TR.2.2: Concurrent User Support:** The architecture must support at least 50 concurrent simulated users without a degradation in response time.

#### 4.3.3 Security Protocols
*   **TR.3.1: Password Hashing:** User passwords must never be stored in plain text; the system must use the BCrypt hashing algorithm with a minimum work factor of 10.
*   **TR.3.2: API Security:** All non-authentication endpoints must be protected, requiring a valid JWT in the Authorization header.