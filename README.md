# GitHub Access Report System

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen.svg)
![Java](https://img.shields.io/badge/Java-17-blue.svg)
![WebFlux](https://img.shields.io/badge/Spring%20WebFlux-Reactive-blueviolet.svg)

A production-ready Spring Boot backend application that connects to the GitHub REST API. It dynamically retrieves all repositories for a given organization alongside their respective collaborators, securely aggregating the data into a streamlined, high-performance REST access report.

## 🚀 Project Overview

The **GitHub Access Report System** is designed to map GitHub users to the repositories they hold access constraints over within an organization. Fetching data for large organizations sequentially is extremely slow due to massive overhead. This service utilizes **Java `CompletableFuture`** and **Spring WebFlux's `WebClient`** to parallelize pagination and network payload retrieval, ensuring that reports for 100+ repositories and 1000+ users are generated in a fraction of the time.

## ✨ Features
- **Reactive Outbound Fetching**: Leverages `WebClient` for fast, non-blocking outbound HTTP API calls to GitHub.
- **Aggressive Parallel Processing**: Maps API calls using `CompletableFuture.runAsync` to concurrently fetch thousands of collaborators across all organization repositories.
- **Deep Pagination Support**: Iterative loop-based dynamic fetching handles the `per_page=100` constraints perfectly.
- **Resilient In-Memory Caching**: Spring `Caffeine` `@Cacheable` prevents GitHub token API limit burnout during repeated identical queries.
- **Global Error Handling**: `@ControllerAdvice` gracefully intercepts HTTP limits (403), authentication misconfigurations (401), or missing organizations (404) and returns clean JSON error structures instead of ugly stack traces. 

## 🛠️ Tech Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.2.4
- **Web**: Spring Web (MVC) + Spring WebFlux (WebClient)
- **Concurrency**: `java.util.concurrent.CompletableFuture` + `ConcurrentHashMap`
- **Caching**: Caffeine Cache
- **Testing**: JUnit 5 + Mockito + Spring Boot Test
- **Build Tool**: Maven

## ⚙️ Setup Steps

### 1. Prerequisites
- Java 17 or higher installed (`java -version`).
- A GitHub Personal Access Token (PAT).
  - *Must have `read:org` and `repo` permissions to index private metadata.*

### 2. Configure the Application
Open `src/main/resources/application.yml` and provide your actual PAT and targeted organization. 
**Do NOT commit your real token to GitHub.**

```yaml
github:
  token: ghp_YOUR_ACTUAL_GITHUB_TOKEN
  org: spring-projects # Replace with your target org
```

### 3. Build and Run
Using your preferred IDE (IntelliJ, Eclipse, VSCode) or the terminal, compile and run the Spring Boot Application:

```bash
mvn clean install
mvn spring-boot:run
```
The server will bound to port `8080`.

## 📡 API Usage

Once the application is running, querying the access report is heavily simplified.

### Request
```http
GET http://localhost:8080/access-report
```

### Example Output
The API responds with `application/json` automatically grouping repository access by user `logins`.

```json
{
  "octocat": [
    "Hello-World",
    "Spoon-Knife"
  ],
  "torvalds": [
    "linux",
    "git"
  ],
  "defunkt": [
    "github-services"
  ]
}
```
