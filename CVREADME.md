Of course\! Here's a complete documentation explaining the "Band Management System" codebase.

This document provides a comprehensive overview of the Band Management System, a Java Spring Boot application designed for managing the members, inventory, and activities of a musical band.

-----

## 1\. Project Overview

The **Band Management System** is a web application that serves as a central hub for a community band. It provides distinct functionalities for different user roles: regular **Members** (parents), **Committee** members, and a **Director**.

The system's core features include:

  * **User Management**: Registration, login, and role-based access control.
  * **Member & Family Management**: Members can manage their own details and add/view their children's profiles.
  * **Inventory Control**: Committee members and the Director can manage the band's inventory of instruments, sheet music, and miscellaneous items.
  * **Loan System**: A system for tracking the loan and return of instruments to both adult and child members.
  * **Performance Management**: The ability to schedule performances, create playlists, and for members to confirm their participation.
  * **Administrative Functions**: The Director has overarching control to manage band rosters, committee members, and approve/deny requests.

### Technology Stack

  * **Backend**: Java 21+ with the Spring Boot framework.
  * **Security**: Spring Security for authentication and role-based authorization.
  * **Database**: Spring Data JPA for object-relational mapping (ORM), likely with a relational database like PostgreSQL or H2.
  * **Frontend**: Thymeleaf for server-side template rendering, along with standard HTML, CSS, and JavaScript for client-side interactivity.
  * **Build Tool**: Gradle for dependency management and building the project.
  * **Testing**: JUnit 5 and Mockito for unit and integration testing.

-----

## 2\. Project Structure

The codebase follows a standard layered architecture, separating concerns into distinct packages.

```plaintext
.
├── build.gradle              // Gradle build script
├── gradlew, gradlew.bat      // Gradle Wrapper scripts
├── Procfile, system.properties // Heroku deployment files
├── src
│   ├── main
│   │   ├── java/uk/ac/sheffield/team28/team28
│   │   │   ├── Team28Application.java  // Main application entry point
│   │   │   ├── config                  // Security configuration
│   │   │   ├── controller              // MVC Controllers (handle web requests)
│   │   │   ├── dto                     // Data Transfer Objects
│   │   │   ├── model                   // JPA Entities (database tables)
│   │   │   ├── repository              // Spring Data JPA Repositories (database access)
│   │   │   └── service                 // Business logic layer
│   │   └── resources
│   │       ├── application.properties  // Application configuration
│   │       ├── static                  // CSS, JavaScript, images
│   │       └── templates               // Thymeleaf HTML views
│   └── test                          // Unit and integration tests
└── ...
```

-----

## 3\. Backend Architecture (MVC & Service Layer)

The application is built on the Model-View-Controller (MVC) pattern, with a service layer handling the business logic.

### 3.1. Model (`model` package)

These are the core data structures, represented as JPA entities that map directly to database tables.

  * `Member`: The central user entity. It stores login credentials, personal details, and role (`MemberType`: **ADULT**, **COMMITTEE**, **DIRECTOR**). It has a one-to-many relationship with `ChildMember`.
  * `ChildMember`: Represents a child linked to a parent `Member`. They can be assigned to a `BandInPractice`.
  * `Item`: A generic base entity for any inventory item. Key properties include `itemType` (**Instrument**, **Music**, **Misc**).
  * `Instrument`, `Music`, `Misc`: These entities extend the concept of an `Item` with specific attributes (e.g., an `Instrument` has a `serialNumber`).
  * `Loan`: Tracks an `Item` being loaned to either a `Member` or a `ChildMember`, including loan and return dates.
  * `Performance`: Defines a band event with a name, venue, date, and the band involved. It holds a playlist (a list of `Music` entities) and tracks participation.
  * `MemberParticipation`: A join table entity linking a `Member` to a `Performance` and storing whether they will participate.
  * `Request`: Stores a user's request to update their account details, which must be approved by a Director.

### 3.2. Repository (`repository` package)

These are Spring Data JPA interfaces that provide an abstraction layer for database operations. They offer built-in CRUD (Create, Read, Update, Delete) methods and allow for the definition of custom database queries using either method naming conventions or the `@Query` annotation.

### 3.3. Service (`service` package)

This layer contains the application's business logic. Controllers delegate tasks to services, which then coordinate with repositories to manipulate data.

  * `MemberService`: Manages all logic related to members, including registration, finding users, role changes (promotion/demotion), and band assignments.
  * `CustomUserDetailsService`: A core part of Spring Security. It implements the `UserDetailsService` interface to fetch user data (`Member`) from the database during the authentication process.
  * `LoanService`: Encapsulates the rules for loaning and returning instruments, ensuring an item cannot be loaned out if it's already on loan.
  * `PerformanceService`: Handles the creation of performances and the logic for members to opt in or out of participation.
  * Other services (`InstrumentService`, `MusicService`, `RequestService`, etc.) manage the business logic for their respective domains.

### 3.4. Controller (`controller` package)

Controllers are the entry point for user interactions. They handle incoming HTTP requests, call the appropriate service methods, and return a view (Thymeleaf template) or data (JSON) to the user.

  * `AuthController`: Manages the public-facing registration and login endpoints.
  * `DashboardController`: The primary controller for logged-in users, displaying a dashboard tailored to their role and band membership.
  * `DirectorController`: Provides endpoints for the Director to perform high-level administrative tasks, such as managing all members, committee roles, and band rosters.
  * `CommitteeController`: Handles the committee-specific dashboard, focusing on inventory management.
  * `AccountDetailsController`: Allows users to view and request changes to their personal information.
  * `PerformanceController`, `LoanController`, `ItemController`, `MusicController`: These are primarily `@RestController`s that serve JSON data, often used by frontend JavaScript to dynamically update parts of a page without a full reload (AJAX).

### 3.5. Configuration & Security (`config` and `security` packages)

  * `SecurityConfig.java`: This is the heart of the application's security. It defines:
      * **Authorization Rules**: Specifies which URLs are publicly accessible (`/auth/login`, `/auth/register`) and which require specific roles (`/committee/**` requires COMMITTEE or DIRECTOR, `/director/**` requires DIRECTOR).
      * **Login Form**: Configures the custom login page, the URL for processing login attempts, the redirect URL on success, and a `CustomAuthenticationFailureHandler`.
      * **Password Encoding**: Declares a `BCryptPasswordEncoder` bean, ensuring all passwords are securely hashed before being stored.
  * `CustomAuthenticationFailureHandler.java`: Provides user-friendly error messages on the login page if authentication fails.

-----

## 4\. Frontend (Views & Static Assets)

The user interface is built with Thymeleaf for server-side rendering, enhanced with client-side JavaScript.

  * **`templates` Directory**: Contains all the HTML files.
      * **Views**: Pages like `dashboard.html`, `login.html`, and `committee-dashboard.html` define the structure of what the user sees.
      * **Fragments (`fragments/`)**: Reusable HTML snippets like `navbar.html` and `footer.html` are included in multiple pages to avoid code duplication.
  * **`static` Directory**:
      * **CSS (`css/`)**: Stylesheets for the application's look and feel.
      * **JavaScript (`js/`)**: Client-side scripts that add dynamic behavior. For example, `dashboardLoan.js` likely handles the AJAX calls to the `LoanController` to process instrument loans without reloading the entire dashboard. `confirmDelete.js` provides confirmation dialogs before a user deletes an item.

-----

## 5\. Key Workflows

### User Authentication

1.  A new user navigates to `/auth/register`. The `AuthController` displays the `register.html` view.
2.  The user submits the form. `AuthController` calls `MemberService` to validate the data (e.g., check if the email is already in use, validate password strength) and save the new `Member`.
3.  The user goes to `/auth/login`. On submission, Spring Security intercepts the request.
4.  It uses `CustomUserDetailsService` to fetch the `Member` from the database by email.
5.  It compares the submitted password with the hashed password in the database using the `BCryptPasswordEncoder`.
6.  On success, the user is redirected to `/dashboard`. On failure, they are sent back to the login page with an error message via the `CustomAuthenticationFailureHandler`.

### Loaning an Instrument

1.  A Committee member or Director is on the `committee-dashboard.html` page.
2.  They select an instrument and a member to loan it to.
3.  A JavaScript function (e.g., in `dashboardLoan.js`) sends a POST request to the `/loans/loanAction` endpoint, handled by `LoanController`.
4.  `LoanController` calls `LoanService`, which validates that the instrument is in storage.
5.  `LoanService` creates a new `Loan` record and updates the `Item`'s `inStorage` status to `false`.
6.  The controller then redirects back to the dashboard, which now reflects the updated loan status.

-----

## 6\. Testing

The `src/test` directory demonstrates a commitment to quality assurance with a robust testing suite.

  * **Controller Tests (`@WebMvcTest`)**: These tests focus on the web layer. They mock the service layer to verify that controllers handle requests correctly, return the right views, and have proper security configurations.
  * **Service Tests (Plain JUnit/Mockito)**: These are unit tests for the business logic. They mock the repository layer to ensure that services perform their logic correctly in isolation.
  * **Repository Tests (`@DataJpaTest`)**: These are integration tests that check if the JPA repositories are correctly configured to interact with an in-memory or test database.
