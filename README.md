# Person Management System

## Project Description

The Person Management System is a microservices-based application designed to manage information about different types of individuals such as employees, students, and retirees. The system consists of three main services that communicate with each other using OpenFeign. This project is built with a focus on scalability, security, and extensibility.

## Services

### Dictionary Service

The Dictionary Service stores dictionary values such as person type, university names, fields of study, job positions, and other reference data. It provides endpoints to manage these values.

### User Service

The User Service handles user management, including security (authentication and authorization). It stores user information, roles, and passwords.

### Person Service

The Person Service manages information about individuals. It provides endpoints to add, edit, search, and manage persons within the system. The service ensures data integrity and supports advanced search criteria and pagination.

## Features

- **Search Endpoint**: A single endpoint to search for people based on various criteria such as type, name, age, PESEL (Polish national identification number), gender, height, weight, and email address. For employees, additional search criteria include salary range. For students, criteria include the current university name.
- **Add Person Endpoint**: A single endpoint to add any type of person with data validation and error handling. The system is designed to allow the addition of new person type without modifying existing classes.
- **Edit Person Endpoint**: A single endpoint to edit person information with concurrency control to handle data conflicts.
- **Manage Employee Positions Endpoint**: An endpoint to manage employee positions with date range validation to prevent overlapping positions.
- **CSV Import Endpoint**: An endpoint to people persons from a CSV file. The import process is non-blocking, supports large files (e.g., 3GB), and provides a status endpoint to check the import progress.

### Security

- Endpoints are secured using Spring Security.
- Only users with the ADMIN role can add or edit persons.
- Only users with the ADMIN or IMPORTER role can import persons from a CSV file.
- Only users with the ADMIN or EMPLOYEE role can manage employee positions.
