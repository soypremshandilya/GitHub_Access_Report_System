# GitHub Access Report System

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen.svg)
![Java](https://img.shields.io/badge/Java-17-blue.svg)
![WebFlux](https://img.shields.io/badge/Spring%20WebFlux-Reactive-blueviolet.svg)

This is a production-grade Spring Boot backend service integrated with the GitHub REST API. It dynamically collects every repository in a specified organization together with each repository’s collaborators, then securely consolidates that information into an efficient, high-performance REST access report.

## 🚀 Project Overview

The **GitHub Access Report System** is built to associate GitHub users with the repositories they can access inside an organization. Pulling this data sequentially for large organizations is very slow because of heavy overhead. To solve this, the service uses **Java `CompletableFuture`** and **Spring WebFlux `WebClient`** to parallelize both pagination and network retrieval, allowing reports for 100+ repositories and 1000+ users to be produced much faster.

## ✨ Features

- **Reactive Outbound Fetching**: Uses `WebClient` to perform fast, non-blocking outbound HTTP calls to GitHub.
- **Aggressive Parallel Processing**: Employs `CompletableFuture.runAsync` to execute API calls concurrently and fetch thousands of collaborators across all organization repositories.
- **Deep Pagination Support**: Dynamic loop-driven retrieval cleanly handles the `per_page=100` pagination limits.
- **Resilient In-Memory Caching**: Spring `Caffeine` with `@Cacheable` helps avoid exhausting GitHub token rate limits for repeated identical requests.
- **Global Error Handling**: `@ControllerAdvice` cleanly handles rate limits (403), authentication configuration issues (401), and missing organizations (404), returning structured JSON errors instead of raw stack traces.

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

- Java 17 or newer installed (`java -version`).
- A GitHub Personal Access Token (PAT).
  - _It must include `read:org` and `repo` scopes to index private metadata._

### 2. Configure the Application

Edit `src/main/resources/application.yml` and set your real PAT plus the organization you want to target.
**Do NOT commit your real token to GitHub.**

```yaml
github:
  token: ghp_YOUR_ACTUAL_GITHUB_TOKEN
  org: spring-projects # Replace with your target org
```

### 3. Build and Run

From your preferred IDE (IntelliJ, Eclipse, VSCode) or from the terminal, build and run the Spring Boot application:

```bash
mvn clean install
mvn spring-boot:run
```

The server will run on port `8080`.

## 📡 API Usage

After the application starts, requesting the access report is straightforward.

### Request

```http
GET http://localhost:8080/access-report
```

### Example Output

The API returns `application/json`, automatically grouping repository access by user `logins`.

```json
{
  "octocat": ["Hello-World", "Spoon-Knife"],
  "torvalds": ["linux", "git"],
  "defunkt": ["github-services"]
}
```
