## 6. Case Scenarios and User Narrative

This section provides detailed, step-by-step walkthroughs of the Cloud-Native Expense Tracker from the perspective of different user personas. These scenarios serve to validate the system’s functional requirements and demonstrate how the microservices architecture handles real-world user interactions.

### 6.1 User Persona: "The Budget-Conscious Professional"
**Profile:** Sarah is a mid-career professional who needs to track her discretionary spending to save for a home down payment. She is tech-savvy but values speed and simplicity in her financial tools.

#### Scenario 6.1.1: Secure Onboarding and Authentication
1.  **Initial Action:** Sarah navigates to the application URL and selects "Sign Up."
2.  **User Input:** Sarah enters her email address and a strong password.
3.  **Frontend Logic:** The React application performs client-side validation (e.g., checking for valid email format and password complexity).
4.  **Backend Interaction:**
    *   The **API Gateway** routes the request to the **Auth Service**.
    *   The Auth Service checks if the email already exists in the PostgreSQL database.
    *   If unique, the service hashes the password using **BCrypt** and saves the new user record.
5.  **Final State:** Sarah is redirected to the Login page with a success message. Upon logging in, she receives a **JWT**, which the Frontend stores securely to authorize future requests.

#### Scenario 6.1.2: Real-Time Expense Categorization
1.  **Context:** Sarah just spent $65.00 on groceries and wants to log it immediately.
2.  **Action:** Sarah opens the dashboard, clicks "Add Expense," and enters the details.
3.  **System Interaction:**
    *   Sarah selects the "Groceries" category (a system default).
    *   The **Expense Service** receives the request, validates the JWT, and creates the record.
    *   The service also triggers an internal notification or update to the **Analytics Service** to recalculate Sarah's grocery spending for the month.
4.  **Outcome:** The dashboard updates in real-time, showing Sarah her new balance and how much of her monthly "Food" budget remains.

### 6.2 User Persona: "The Data-Driven Analyst"
**Profile:** Mark is a freelance consultant who uses multiple categories to track business expenses for tax purposes. He relies heavily on the "Reports" feature to summarize his quarterly spending.

#### Scenario 6.2.1: Generating Quarterly Spending Reports
1.  **Initial Action:** Mark navigates to the "Analytics" tab and selects a custom date range (the last three months).
2.  **System Processing:**
    *   The **Analytics Service** aggregates data from the PostgreSQL database, filtering by Mark’s `user_id` and the specific date range.
    *   The service calculates totals for each category and identifies the "Highest Spending Category."
3.  **Visualization:** The React Frontend receives the aggregated JSON data and renders a high-definition **Category Distribution Chart** (using a library like Recharts or Chart.js).
4.  **Value Provided:** Mark sees that 40% of his expenses were for "Office Supplies" and can easily export this summary for his tax records.

### 6.3 System Integrity & Error Scenarios
To ensure a production-grade experience, the system is designed to handle edge cases and unauthorized access attempts gracefully.

#### Scenario 6.3.1: Handling Service Downtime (Resilience)
1.  **Context:** The **Analytics Service** is temporarily undergoing a container update and is briefly offline.
2.  **User Action:** Sarah logs in and attempts to view her dashboard.
3.  **System Response:**
    *   The **API Gateway** detects that the Analytics Service is unreachable.
    *   Rather than crashing the entire frontend, the Gateway returns a "Service Unavailable" status for the analytics widget only.
    *   The **Expense Service** remains fully functional, allowing Sarah to continue logging new expenses without interruption.
4.  **Outcome:** Sarah receives a polite notification that "Reports are currently being updated," demonstrating the benefits of our decoupled microservices architecture.

#### Scenario 6.3.2: Defending Against Injection Attacks
1.  **Action:** An attacker attempts to submit a malicious SQL string into the "Description" field of an expense.
2.  **System Defense:**
    *   The **Spring Boot Backend** utilizes **Spring Data JPA** with parameterized queries.
    *   The malicious input is treated as a literal string rather than executable code.