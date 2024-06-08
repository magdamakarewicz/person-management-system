# User Service

## Table of Contents
- [Project Description](#project-description)
- [Features](#features)
- [REST API Endpoints](#rest-api-endpoints)
  - [User Endpoints](#user-endpoints)
  - [Role Endpoints](#role-endpoints)
- [Security](#security)
- [Setup and Installation](#setup-and-installation)
  - [Prerequisites](#prerequisites)
  - [Running the Application](#running-the-application)
  - [Testing](#testing)
- [API Documentation](#api-documentation)

## Project Description

The User Service is a microservice within the Person Management System. It is responsible for managing application users, including their usernames, passwords, and roles. The service ensures secure handling of user credentials by encoding passwords with `PasswordEncoder` and storing all information in a database. Comprehensive tests have been implemented to ensure the reliability of the service.

## Features

- **User Management**:
    - Create, read, update, and delete users.
    - Assign and remove roles for users.
    - Securely handle and encode passwords.

- **Role Management**:
    - Create, read, update, and delete roles.
    - Retrieve all users assigned to a specific role.

## REST API Endpoints

### User Endpoints

- **Get All Users**
  `GET /api/users`
  Retrieves a list of all users.

- **Get User by ID**
  `GET /api/users/{userId}`
  Retrieves a user by their ID.

- **Get User by Username**
  `GET /api/users/byUsername?username={username}`
  Retrieves a user along with their roles by their username.

- **Create User**
  `POST /api/users`
  Adds a new user.

- **Update User Password**
  `PUT /api/users/{userId}`
  Updates the password of an existing user.

- **Delete User by ID**
  `DELETE /api/users/{userId}`
  Deletes a user by their ID.

- **Assign Role to User**
  `POST /api/users/{userId}/roles/{roleId}`
  Assigns a role to a user.

- **Remove Role from User**
  `DELETE /api/users/{userId}/roles/{roleId}`
  Removes a role from a user.

### Role Endpoints

- **Get All Roles**
  `GET /api/roles` 
  Retrieves a list of all roles.

- **Get Role by ID**
  `GET /api/roles/{roleId}`
  Retrieves a role by its ID.

- **Create Role**
  `POST /api/roles`
  Adds a new role.

- **Update Role**
  `PUT /api/roles/{roleId}`
  Updates the name of an existing role.

- **Delete Role by ID**
  `DELETE /api/roles/{roleId}`
  Deletes a role by its ID.

- **Get Users by Role**
  `GET /api/roles/{roleId}/users`
  Retrieves all users assigned to a specific role.
  
## Security

- Passwords are securely encoded using `PasswordEncoder`.
- Endpoints are protected with role-based access control:
- Only users with the `ADMIN` role can manage users and roles.

## Setup and Installation

### Prerequisites

- JDK 17
- Maven 3.8+
- MySQL Database

### Running the Application

1. Configure the database settings in `application.properties`:
- **Test Database**: H2
- **Production Database**: MySQL

2. Build and run the service:

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
   
3. The service will run on port 8081 by default. You can access it at http://localhost:8081.

### Testing

## Unit and Integration Tests

The application includes comprehensive unit and integration tests to ensure functionality and stability. These tests are executed during the build process.

## API Documentation

The API documentation for the User Service, created using OpenAPI, can be accessed at http://localhost:8081/swagger-ui/index.html. This documentation provides a detailed overview of all available endpoints, their parameters, and responses, enabling easy exploration and testing of the API.