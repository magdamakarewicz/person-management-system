# Person Management System

## Table of Contents
- [Project Description](#project-description)
- [Services](#services)
  - [Dictionary Service](#dictionary-service)
  - [User Service](#user-service)
  - [Person Service](#person-service)
- [Features](#features)
- [Security](#security)
- [Technologies Used](#technologies-used)
- [Known Issues and Upcoming Changes](#known-issues-and-upcoming-changes)
- [Documentation](#documentation)
  
## Project Description

The Person Management System is a microservices-based application designed to manage information about different types of individuals such as employees, students, retirees, and potentially other types of individuals. The system consists of three main services that communicate with each other using OpenFeign. This project is built with a focus on scalability, security, and extensibility.

## Services

### Dictionary Service

The Dictionary Service stores dictionary values such as person types, university names, fields of study, job positions, and other reference data. It provides endpoints to manage these values.

### User Service

The User Service handles user management, including security (authentication and authorization). It stores user information, roles, and passwords.

### Person Service

The Person Service manages information about individuals. It provides endpoints to add, edit, search, and manage people within the system. The service ensures data integrity and supports advanced search criteria and pagination.

## Features

- **Search Endpoint**: A single endpoint to search for people based on various criteria such as type, name, age, PESEL (Polish national identification number), gender, height, weight, and email address. For employees, additional search criteria include salary range. For students, criteria include the current university name.
- **Add Person Endpoint**: A single endpoint to add any type of person with data validation and error handling. The system is designed to allow the addition of new person type without modifying existing classes.
- **Edit Person Endpoint**: A single endpoint to edit person information with concurrency control to handle data conflicts.
- **Manage Employee Positions Endpoint**: An endpoint to manage employee positions with date range validation to prevent overlapping positions.
- **CSV Import Endpoint**: An endpoint to import people from a CSV file. The import process is non-blocking, and provides a status endpoint to check the import progress.

## Security

- Endpoints are secured using Spring Security.
- Only users with the ADMIN role can add or edit persons.
- Only users with the ADMIN or IMPORTER role can import persons from a CSV file.
- Only users with the ADMIN or EMPLOYEE role can manage employee positions.

## Technologies Used

- **Java 17**: For writing the application.
- **Spring Boot**: For building the application.
- **Spring Security**: For securing the application.
- **Spring Data JPA**: For database access.
- **Hibernate**: For ORM (Object-Relational Mapping).
- **MySQL**: As the primary database.
- **H2**: For in-memory database testing.
- **Liquibase**: For database migrations.
- **ModelMapper**: For object mapping.
- **JUnit**: For unit testing.
- **Mockito**: For mocking in tests.
- **Spring Cloud OpenFeign**: For inter-service communication.
- **Lombok**: To reduce boilerplate code.
- **Maven**: For build automation.
- **Postman**: For API testing.
- **REST API**: For service communication and integration.

## Known Issues and Upcoming Changes

- **API Documentation**: Currently, there are compatibility issues with Swagger and the latest version of Spring. As a result, Swagger will soon be replaced by OpenAPI. This transition will ensure ongoing support and provide enhanced features for API documentation.

## Documentation

Each service has its own README.md file with detailed descriptions, setup instructions, and API documentation:

- [Dictionary Service README](dictionary-service/README.md)
- [User Service README](user-service/README.md)
- [Person Service README](person-service/README.md)
