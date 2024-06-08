# Person Service

## Table of Contents
- [Project Description](#project-description)
- [Features](#features)
- [REST API Endpoints](#rest-api-endpoints)
    - [Person Endpoints](#person-endpoints)
    - [Employee Position Endpoints](#employee-position-endpoints)
- [Integration with Other Services](#integration-with-other-services)
- [Design Patterns](#design-patterns)
- [Validation and Exception Handling](#validation-and-exception-handling)
- [Security](#security)
- [Setup and Installation](#setup-and-installation)
- [Testing](#testing)
- [Example Data](#example-data)
- [Technical Documentation](#technical-documentation)
- [API Documentation](#api-documentation)
  
## Project Description

The Person Service is a microservice within the Person Management System responsible for managing information about people. It includes employees, students, retirees, and potentially other types of individuals. Each person has basic attributes such as first name, last name, PESEL (unique), height, weight, and email address. Additional attributes are stored depending on the person's type:
- For students: university name, enrollment year, field of study, and scholarship amount.
- For employees: start date, current position, and salary.
- For retirees: retirement pension amount and years of work.

## Features

- **Person Management**:
    - Add, retrieve, update, and delete people of various types.
    - Flexible search criteria based on type, first name, last name, PESEL, gender, height, weight, and email address.
    - Pagination support for search results.

- **Employee Position Management**:
    - Assign, retrieve, update, and delete positions for employees.
    - Ensure non-overlapping date ranges for positions.

- **Data Import**:
    - Import people from a CSV file asynchronously.
    - Monitor import progress with status endpoint.

## REST API Endpoints

### Person Endpoints

- **Get People**
  `GET /api/people`
  Retrieves a list of people based on specified parameters, allowing for flexible search criteria and pagination.

- **Get Person by ID**
  `GET /api/people/{id}`
  Retrieves a person by their ID.

- **Add Person**
  `POST /api/people`
  Adds a new person to the system.

- **Add Person Type**
  `POST /api/people/type`
  Adds a new person type to the 'type' dictionary.

- **Update Person**
  `PUT /api/people/{id}`
  Updates the information of an existing person.

- **Import People**
  `POST /api/people/import`
  Imports data from a CSV file asynchronously.

- **Get Import Status**
  `GET /api/people/import/status`
  Retrieves the status of the ongoing data import process.

- **Delete Person by ID**
  `DELETE /api/people/{id}`
  Deletes a person by their ID.

### Employee Position Endpoints

- **Add Position to Employee**
  `POST /api/employees/{employeeId}/positions`
  Adds a new position to an employee.

- **Update End Date for Current Position**
  `PATCH /api/employees/{employeeId}/positions/{positionId}`
  Updates the end date for the current position of an employee.

- **Get All Employee Positions**
  `GET /api/employees/{employeeId}/positions`
  Retrieves all positions of an employee.

- **Get Employee Position by ID**
  `GET /api/employees/{employeeId}/positions/{positionId}`
  Retrieves a specific position of an employee by its ID.

- **Delete Employee Position by ID**
  `DELETE /api/employees/{employeeId}/positions/{positionId}`
  Deletes a position of an employee by its ID.

## Integration with Other Services

The Person Service integrates with the following services using OpenFeign:
- **Dictionary Service**: Fetches dictionary values.
- **User Service**: Retrieves application users and their roles for authentication and authorization purposes.

## Design Patterns

The Person Service uses the Factory Pattern to handle operations on specific types of people. This allows for the addition of new person types without modifying existing classes, providing a flexible and scalable design.

## Validation and Exception Handling

- **Custom Validators**: Implemented for validating various attributes of people to ensure data integrity and consistency.
- **Exception Handling**: Comprehensive exception handling to manage errors and provide meaningful responses to the client.

## Security

Endpoints are secured with role-based access control:
- Only users with the `ADMIN` role can add, update, and delete people, import data, and manage employee positions.
- Users with appropriate roles can perform specific operations.

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
3. The service will run on port 8083 by default. You can access it at http://localhost:8083.

### Testing

## Unit and Integration Tests

The application includes comprehensive unit and integration tests to ensure functionality and stability. These tests are executed during the build process.

## Example Data

Example CSV files for importing people is located in the resources/importdata folder. This can be used to test the import functionality using the provided endpoints.

# Technical Documentation

## Feign Clients

Classes in the api package annotated with @FeignClient contain technical documentation within the class, explaining their operation. Below is an example:

```java
/**
* This Feign client allows you to access dictionary data from the 'dictionary-service'.
* The 'dictionary-service' provides dictionaries for various purposes.
*
* Dictionary Information:
* - Dictionary with ID 1: 'types'
* - Dictionary with ID 2: 'positions'
* - Dictionary with ID 3: 'university names'
* - Dictionary with ID 4: 'fields of study'
*/
@FeignClient(name = "dictionary-service", url = "http://localhost:8082")
public interface DictionaryServiceClient { 
        
    /**
    * Retrieves a dictionary value by its ID.
    *
    * @param dictionaryValueId The ID of the dictionary value to retrieve.
    * @return A DictionaryValueSimpleDto representing the dictionary value.
    */
    @GetMapping("/api/dictionaryvalues/{dictionaryValueId}")
    DictionaryValueSimpleDto getDictionaryValueById(@PathVariable("dictionaryValueId") Long dictionaryValueId);

    /**
    * Retrieves a dictionary value by its name and by dictionary ID.
    *
    * @param dictionaryId The ID of the dictionary.
    * @param name The name of the dictionary value to retrieve.
    * @return A DictionaryValueSimpleDto representing the dictionary value.
    */
    @GetMapping("/api/dictionaryvalues/{dictionaryId}/value")
    DictionaryValueSimpleDto getDictionaryValueByDictionaryIdAndName(
    @PathVariable("dictionaryId") Long dictionaryId, @RequestParam String name);

    /**
    * Adds a new value to the 'types' dictionary (ID 1).
    *
    * @param name The name of the value to add to the dictionary.
    * @return A DictionarySimpleDto representing the updated 'types' dictionary.
    */
    @PostMapping("/api/dictionaries/1/value")
    DictionarySimpleDto addValueToTypeDictionary(@RequestBody @Valid @RequestParam String name);

}
```

## Assumptions Regarding Dictionaries

It is assumed that the dictionaries should be added to the database in the appropriate order as indicated in the above documentation.

## Classes Using Clients

Classes utilizing Feign clients also contain documentation explaining their interactions and operations.

# API Documentation

The API documentation for the User Service, created using OpenAPI, can be accessed at http://localhost:8083/swagger-ui/index.html. This documentation provides a detailed overview of all available endpoints, their parameters, and responses, enabling easy exploration and testing of the API.