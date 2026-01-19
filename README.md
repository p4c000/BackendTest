Similar Products API - README
markdown
# Similar Products API

A REST API built with Spring Boot that provides similar product recommendations by aggregating data other microservices.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Running the Application](#running)

---

## Overview

This application exposes a REST API endpoint that returns detailed information about similar products. It aggregates data from two existing backend services:

1. **Similar Products Service** - Returns IDs of similar products
2. **Product Details Service** - Returns detailed information for a specific product

The API fetches similar product IDs, then retrieves detailed information for each product **in parallel** for optimal performance.

---

## Features
- RESTful API design
- Parallel API calls using Spring WebFlux
- Resilient error handling with graceful degradation
- Timeout protection (20 seconds per request)
- Console logging
- Docker-ready environment


---

## Prerequisites

- **Java 17** or higher
- **Maven 3.6+** (or use included wrapper)
- **Git** (for cloning the repository)

### Check Your Java Version

```bash
java -version
# Should show: openjdk version "17.0.x" or higher

Installation
1. Clone the Repository
bash
git clone <repository-url>
cd test

2. Build the Project
bash
# Windows
mvnw clean install


Running the Application
Step 1: Start the mock service, for this task use the instuccions in https://github.com/dalogax/backendDevTest
bash
docker-compose up -d simulado influxdb grafana
This will start:

Simulado (Mock APIs) on http://localhost:3001
InfluxDB (Metrics database) on http://localhost:8086
Grafana (Monitoring dashboard) on http://localhost:3000
Step 2: Verify Mocks Are Running
bash
curl http://localhost:3001/product/1/similarids
Expected response:

json
[2, 3, 4]
Step 3: Start the Application
bash
# Windows
mvnw spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
The application will start on port 5000.

Step 4: Test the API
bash
curl http://localhost:5000/product/1/similar
Expected response:

json
[
  {
    "id": "2",
    "name": "Dress Shirt",
    "price": 99.99,
    "availability": true
  },
  {
    "id": "3",
    "name": "Blazer",
    "price": 199.99,
    "availability": false
  },
  {
    "id": "4",
    "name": "Suit",
    "price": 399.99,
    "availability": true
  }
]

API Documentation
Get Similar Products
Endpoint: GET /product/{productId}/similar

Description: Returns detailed information about products similar to the specified product.

Path Parameters:

Parameter	Type	Required	Description
productId	String	Yes	The ID of the product to find similar items for
Response:

json
[
  {
    "id": "string",
    "name": "string",
    "price": number,
    "availability": boolean
  }
]
Status Codes:

200 OK - Successfully retrieved similar products
500 Internal Server Error - Server error
Examples:

bash
# Get similar products for product ID 1
curl http://localhost:5000/product/1/similar

# Get similar products for product ID 5
curl http://localhost:5000/product/5/similar

# Using HTTP
http GET localhost:5000/product/1/similar

# Using Postman
GET http://localhost:5000/product/1/similar
```
---

## Running
1. Start the Mock APIs
   bash
   docker-compose up -d simulado influxdb grafana
2. Verify Mocks are Working
   bash
   curl http://localhost:3001/product/1/similarids
   You should get a response like: [2, 3, 4]

3. Start Your Spring Boot Application
   bash
   ./mvnw spring-boot:run
   Or run from your IDE

4. Test Your Endpoint Manually
   bash
   curl http://localhost:5000/product/1/similar
