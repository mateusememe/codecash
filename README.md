# API Hero: From Zero to Deploy with Spring Boot and GraphQL

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Java Version](https://img.shields.io/badge/java-25-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-green)

Welcome to the official source code repository for the **API Hero** course! This project is a complete, production-ready transactional API designed to teach modern backend development practices with Java and the Spring ecosystem.

## About The Project

This repository contains the final application built during the course. We will create a financial transactions API from the ground up, covering everything from initial setup to implementing core business logic and optimizing for performance.

The main features include:
* User and account management.
* Performing atomic financial transactions between accounts.
* A complete GraphQL interface for all operations.
* Performance optimization using a Redis-based cache.
* Robust database versioning with Flyway.

## Tech Stack

This project is built using a modern, powerful tech stack, as outlined in the course plan:
* **Language**: Java 25
* **Framework**: Spring Boot 3.5.x
* **API**: Spring for GraphQL
* **Database**: PostgreSQL
* **Database Access**: Spring Data JPA / Hibernate
* **Caching**: Spring Cache with Redis
* **Database Migrations**: Flyway
* **Containerization**: Docker & Docker Compose
* **Utilities**: Lombok

## Prerequisites

Before you begin, ensure you have the following software installed on your machine.
* JDK 25 (Java Development Kit)
* Docker and Docker Compose
* A Java IDE, such as IntelliJ IDEA Community Edition
* Git

## Getting Started

To get a local copy up and running, follow these simple steps.

1.  **Clone the repository:**
    ```sh
    git clone [https://github.com/mateusememe/codecash.git](https://github.com/mateusememe/codecash.git)
    cd api-hero
    ```

2.  **Start the infrastructure:**
    This command will start the PostgreSQL database and Redis cache in Docker containers.
    ```sh
    docker-compose up -d
    ```

3.  **Run the Spring Boot application:**
    You can run the application using the Maven wrapper included in the project.
    ```sh
    ./mvnw spring-boot:run
    ```
    The API server will start on `http://localhost:8080`.

4.  **Access the GraphQL Playground:**
    Once the application is running, open your web browser and navigate to the GraphiQL interface to interact with the API.
    ```
    http://localhost:8080/graphiql
    ```

## Example API Usage

Here are a few example mutations you can run in the GraphiQL interface to test the API.

#### Create a User
This mutation creates a new user and an associated account with a starting balance.

```graphql
mutation {
  createUser(input: {
    name: "Your Name",
    email: "your.email@example.com",
    document: "123.456.789-00",
    pass: "securePassword123"
  }) {
    id
    name
    email
  }
}