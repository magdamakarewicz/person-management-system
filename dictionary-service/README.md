# Dictionary Service

## Table of Contents
- [Project Description](#project-description)
- [Features](#features)
- [REST API Endpoints](#rest-api-endpoints)
    - [Dictionary Endpoints](#dictionary-endpoints)
    - [Dictionary Value Endpoints](#dictionary-value-endpoints)
- [Technologies Used](#technologies-used)
- [Known Issues and Upcoming Changes](#known-issues-and-upcoming-changes)
- [Setup and Installation](#setup-and-installation)
    - [Prerequisites](#prerequisites)
    - [Running the Application](#running-the-application)
    - [Testing](#testing)

## Project Description

The Dictionary Service is a microservice within the Person Management System. It is responsible for storing and managing dictionaries and their values, such as field of study, job position, university, person type. The service is thoroughly tested to ensure reliability.

## Features

- **Dictionary Management**:
    - Create, read, update, and delete dictionaries.
    - Add and remove values to/from dictionaries.

- **Dictionary Value Management**:
    - Create, read, update, and delete dictionary values.
    - Retrieve dictionary values by dictionary ID and name.

## REST API Endpoints

### Dictionary Endpoints

- **Get All Dictionaries**
  `GET /api/dictionaries`
  Retrieves a list of all dictionaries.

- **Get Dictionary by ID**
  `GET /api/dictionaries/{dictionaryId}`
  Retrieves a dictionary by its ID.

- **Create Dictionary**
  `POST /api/dictionaries`
  Adds a new dictionary.

- **Update Dictionary Name**
  `PUT /api/dictionaries/{dictionaryId}`
  Updates the name of an existing dictionary.

- **Delete Dictionary by ID**
  `DELETE /api/dictionaries/{dictionaryId}`
  Deletes a dictionary by its ID.

- **Add Value to Dictionary**
  `POST /api/dictionaries/{dictionaryId}/values/{dictionaryValueId}`
  Adds a value to an existing dictionary.

- **Add Value to 'Type' Dictionary**
  `POST /api/dictionaries/1/values`
  Adds a value to the 'type' dictionary (ID = 1).

- **Remove Value from Dictionary**
  `DELETE /api/dictionaries/{dictionaryId}/values/{dictionaryValueId}`
  Removes a value from a dictionary.

- **Remove All Values from Dictionary**
  `DELETE /api/dictionaries/{dictionaryId}/values`
  Removes all values from a dictionary.

- **Get Values by Dictionary ID**
  `GET /api/dictionaries/{dictionaryId}/values`
  Retrieves all dictionary values from a specified dictionary.

### Dictionary Value Endpoints

- **Get All Dictionary Values**
  `GET /api/dictionaryvalues`
  Retrieves a list of all dictionary values.

- **Get Dictionary Value by ID**
  `GET /api/dictionaryvalues/{dictionaryValueId}`
  Retrieves a dictionary value by its ID.

- **Get Dictionary Value by Dictionary ID and Name**
  `GET /api/dictionaryvalues/{dictionaryId}/value?name={name}`
  Retrieves a dictionary value by dictionary ID and value name.

- **Create Dictionary Value**
  `POST /api/dictionaryvalues`
  Adds a new dictionary value.

- **Update Dictionary Value Name**
  `PUT /api/dictionaryvalues/{dictionaryValueId}`
  Updates the name of an existing dictionary value.

- **Delete Dictionary Value by ID**
  `DELETE /api/dictionaryvalues/{dictionaryValueId}`
  Deletes a dictionary value by its ID.

- **Delete Unassigned Dictionary Values**
  `DELETE /api/dictionaryvalues/unassigned`
  Deletes all unassigned dictionary values.

## Technologies Used

- **Spring Boot**: For building the application.
- **Spring Data JPA**: For database access.
- **Hibernate**: For ORM.
- **MySQL**: As the primary database.
- **ModelMapper**: For object mapping.
- **JUnit**: For testing.
- **Lombok**: To reduce boilerplate code.

## Known Issues and Upcoming Changes

- **API Documentation**: There is currently an issue with generating API documentation. Swagger will soon be replaced by OpenAPI due to compatibility issues with the latest version of Spring. This transition will ensure continued support and enhanced features for API documentation.

## Setup and Installation

### Prerequisites

- JDK 17
- Maven 3.8+
- MySQL Database

### Running the Application

1. Configure the database settings in `application.properties`.
2. Build and run the service:

   ```bash
   mvn clean install
   mvn spring-boot:run

### Testing

The User Service includes comprehensive tests. To run the tests, use the following command:

```bash
mvn test