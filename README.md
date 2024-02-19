### Tests and linter status:
![My workflow](https://github.com/a88217/java-project-72/actions/workflows/myWorkflow.yml/badge.svg)

### Codeclimate Maintainability:
[![Maintainability](https://api.codeclimate.com/v1/badges/75932efa13a57b81ea70/maintainability)](https://codeclimate.com/github/a88217/java-project-72/maintainability)

### Codeclimate TestCoverage:
[![Test Coverage](https://api.codeclimate.com/v1/badges/75932efa13a57b81ea70/test_coverage)](https://codeclimate.com/github/a88217/java-project-72/test_coverage)


### Description
Page Analyzer - this is full-fledged website based on the Javelin framework that analyzes specified pages for SEO suitability. It implements the fundamental principles of modern website development using the MVC architecture: handling routing, request handlers, and templating, as well as interacting with a database through ORM.
Technologies involved include:
- Frontend (Bootstrap).
- Javalin Framework (Routing, Views).
- Database (PostgreSQL, H2, JDBC API (Hikari), Migrations, Query Builders).
- Deployment (PaaS). HTTP (including request execution).
- Integration Testing. Logging.
- Linters, running tests, CI.


### Requirements:
Before using this application you must install and configure:
- JDK 20;
- Gradle 8.2


### Run
```bash
./gradlew run
```

### Example of a deployed website on Render.com:
<a href="https://page-analyzer-rs5k.onrender.com">Page Analyzer</a>


