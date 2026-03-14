 ## 9. Project Schedule and Work Breakdown Structure (WBS)
    
    This section outlines the chronological roadmap for the Expense Tracker project. The schedule is divided into six distinct phases, ensuring a logical
    progression from architectural design to final system validation and presentation.
    
    ### 9.1 High-Level Project Milestones
    These milestones represent the critical success factors for the project. Each milestone must be formally verified before the team proceeds to the
    subsequent phase.
    
    | Milestone | Description | Target Date |
    | :--- | :--- | :--- |
    | **M1: Architecture Approval** | Finalization of C4 diagrams and database schema. | Week 2 |
    | **M2: Core Auth Service** | Successful JWT issuance and user persistence. | Week 4 |
    | **M3: CRUD Functionality** | Expense Service fully integrated with Frontend. | Week 6 |
    | **M4: Analytics & Monitoring** | Grafana dashboards active and reporting metrics. | Week 8 |
    | **M5: Full System Integration** | All containers orchestrated via Docker Compose. | Week 10 |
    | **M6: Final Submission** | Project Plan, Codebase, and User Guides complete. | Week 12 |
   
    ### 9.2 Detailed Work Breakdown Structure (WBS)
    
    #### Phase 1: Research & Architectural Design (Weeks 1-2)
    *   **WBS 1.1: Requirement Elicitation:** Refine business, functional, and technical requirements.
    *   **WBS 1.2: System Modeling:** Create C4 architecture diagrams and Entity Relationship Diagrams (ERD).
    *   **WBS 1.3: Tech Stack Initialization:** Set up the GitHub repository and project structure.
   
    #### Phase 2: Foundation & Security (Weeks 3-4)
    *   **WBS 2.1: Auth Service Implementation:** Develop the Spring Boot Auth Service and User entity.
    *   **WBS 2.2: Security Configuration:** Implement BCrypt hashing and JWT generation logic.
    *   **WBS 2.3: API Gateway Setup:** Configure Spring Cloud Gateway for initial routing.
   
    #### Phase 3: Core Feature Development (Weeks 5-7)
    *   **WBS 3.1: Expense Service Development:** Implement CRUD operations for transactions.
    *   **WBS 3.2: Frontend Dashboard:** Build the React UI for listing and adding expenses.
    *   **WBS 3.3: Database Integration:** Ensure data persistence across service restarts.
   
    #### Phase 4: Analytics & Observability (Weeks 8-9)
    *   **WBS 4.1: Analytics Service:** Develop aggregation logic for monthly and category reports.
    *   **WBS 4.2: Data Visualization:** Integrate Chart.js/Recharts into the React frontend.
    *   **WBS 4.3: Monitoring Stack:** Deploy Prometheus and Grafana containers.
   
    #### Phase 5: Testing & DevOps Optimization (Weeks 10-11)
    *   **WBS 5.1: Unit & Integration Testing:** Achieve >80% code coverage on all backend services.
    *   **WBS 5.2: End-to-End Validation:** Perform full-system walkthroughs based on Case Scenarios (Section 6).
    *   **WBS 5.3: Container Optimization:** Refine Dockerfiles for performance and security.
   
    #### Phase 6: Final Documentation & Delivery (Week 12)
    *   **WBS 6.1: User Guide Creation:** Draft a manual for end-users and deployment instructions for admins.
    *   **WBS 6.2: Final Project Review:** Conduct a team audit against the grading rubric.
    *   **WBS 6.3: Submission:** Prepare the final ZIP archive and documentation for grading.
   
    ### 9.3 Project Gantt Chart
    The following chart visualizes the overlapping nature of our development phases, highlighting the parallel tracks for Frontend and Backend
    engineering.