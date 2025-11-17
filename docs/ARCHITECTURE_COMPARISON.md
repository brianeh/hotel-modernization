# Architecture Comparison - Hotel Reservation System

A detailed comparison of architecture patterns, technology stacks, and deployment strategies across the modernization phases.

**Document Purpose:** This document provides side-by-side comparisons of all phases for technical evaluation. It focuses on technology stacks, performance characteristics, costs, and scalability. For phase planning and execution details, see [MODERNIZATION_ROADMAP.md](./MODERNIZATION_ROADMAP.md). For decision-making guidance, see [DECISION_FRAMEWORK.md](./DECISION_FRAMEWORK.md).

## Architecture Evolution Timeline

```mermaid
timeline
    title Technology Stack Evolution
    section Legacy
        Java EE 7    : GlassFish Application Server
                     : JSP + Servlet + EJB
                     : MySQL Database
    section RESTful
        JAX-RS API   : Jackson JSON Provider
                     : CDI Injection
                     : Same Infrastructure
    section SPA
        React/Vue    : Modern Build Tools
                     : S3/CloudFront Deployment
                     : REST API Consumption
    section Spring Boot
        Spring Boot  : Embedded Tomcat
                     : Spring Data JPA
                     : Cloud-Native Deployment
    section Serverless
        Lambda       : DynamoDB NoSQL
                     : API Gateway
                     : Event-Driven Architecture
    section Microservices
        Quarkus      : Kubernetes
                     : Service Mesh
                     : Event-Driven Services
```

## Architecture Patterns Comparison

### Diagram: Evolution from Monolith to Microservices

```mermaid
graph LR
    subgraph P1["Phase 1: Legacy Monolith - hotel-monolith"]
        A[Browser] --> B[JSP/Servlet]
        B --> C[EJB Session Beans]
        C --> D["JPA Entities - EclipseLink"]
        D --> E[MySQL 8.0]
    end
    
    subgraph P2["Phase 2: API Decoupling - hotel-api-rest"]
        F[Browser/JSP] --> G["JAX-RS REST API - Jersey"]
        G --> C2[EJB Session Beans]
        C2 --> D2["JPA Entities - EclipseLink"]
        D2 --> E2[MySQL 8.0]
    end
    
    subgraph P3["Phase 3: SPA Frontend - hotel-ui-react"]
        H[React 18.2 SPA] --> I2[Vite Proxy]
        I2 --> G2[JAX-RS REST API]
        G2 --> C3[EJB Session Beans]
        C3 --> D3["JPA Entities - EclipseLink"]
        D3 --> E3[MySQL 8.0]
        CF1[S3/CloudFront] --> H
        RTR[React Router 7.9.5] -.-> H
    end
    
    subgraph P4["Phase 4: Spring Boot"]
        J2[Angular/React SPA] --> K[Spring Boot REST API]
        K --> L1[Spring Data JPA]
        L1 --> L2[PostgreSQL 15]
        O[CloudFront] --> J2
    end
    
    P1 -.->|Evolution| P2
    P2 -.->|Evolution| P3
    P3 -.->|Evolution| P4
    
    P4 -.->|Evolution| P5
    
    subgraph P5["Phase 5: Serverless"]
        P[React SPA] --> Q[API Gateway]
        Q --> R1[Lambda: Room Service]
        Q --> R2[Lambda: Reservation Service]
        Q --> R3[Lambda: User Service]
        R1 --> S[DynamoDB]
        R2 --> S
        R3 --> T[Cognito]
        U1[S3/CloudFront] --> P
    end
    
    P5 -.->|Evolution| P6
    
    subgraph P6["Phase 6: Microservices"]
        U2[React SPA] --> V[API Gateway]
        V --> W[Service Mesh]
        W --> X1[Room Service]
        W --> X2[Reservation Service]
        W --> X3[User Service]
        W --> X4[Payment Service]
        X1 --> Y1[PostgreSQL]
        X2 --> Y2[PostgreSQL]
        X3 --> Y3[Cognito]
        X4 --> Y4[PostgreSQL]
        Z[CloudFront] --> U2
    end
    
    style A fill:#ff6b6b
    style H fill:#ffe66d
    style CF1 fill:#ffe66d
    style J2 fill:#ffe66d
    style P fill:#ffe66d
    style U2 fill:#ffe66d
    style R1 fill:#4ecdc4
    style R2 fill:#4ecdc4
    style R3 fill:#4ecdc4
    style X1 fill:#95e1d3
    style X2 fill:#95e1d3
    style X3 fill:#95e1d3
    style X4 fill:#95e1d3
```

## Technology Stack Comparison

| Component | Phase 1: Legacy (hotel-monolith) | Phase 2: RESTful (hotel-api-rest) | Phase 3: SPA (hotel-ui-react) | Phase 4: Spring Boot | Phase 5: Serverless | Phase 6: Microservices |
|-----------|----------------|------------------|--------------|---------------------|---------------------|------------------------|
| **Frontend Framework** | JSP (JavaServer Pages) | JSP (unchanged) | React 18.2.0 | Angular/React | React | React |
| **Frontend Build** | JSP Compilation (Ant) | JSP Compilation (Ant) | Vite 5.0.0 | Angular CLI | Vite/Webpack | Vite |
| **Frontend Language** | Java + HTML/CSS/JS | Java + HTML/CSS/JS | TypeScript 5.0.0 | TypeScript/JavaScript | TypeScript/JavaScript | TypeScript/JavaScript |
| **Frontend Router** | N/A | N/A | React Router 7.9.5 | Angular Router/React Router | React Router | React Router |
| **Deployment** | WAR in EAR | WAR in EAR | S3 + CloudFront (planned) | CloudFront + Container | S3 + CloudFront | CloudFront |
| **API Layer** | N/A | JAX-RS (Jersey) + Jackson + CDI | JAX-RS (Jersey) - consumed | Spring MVC | Lambda Handlers | Quarkus REST |
| **Backend Framework** | EJB 3.1 | EJB 3.1 | EJB 3.1 (backend) | Spring Boot 3.x | AWS Lambda | Quarkus |
| **Business Logic** | Session Beans (RoomFacade, ReservationFacade) | Session Beans (reused) | Session Beans (via REST) | Spring Services | Lambda Functions | Microservices |
| **Data Access** | JPA 2.1 (EclipseLink) | JPA 2.1 (EclipseLink) | JPA 2.1 (via REST) | Spring Data JPA | DynamoDB SDK | Quarkus Panache |
| **Database** | MySQL 8.0 | MySQL 8.0 | MySQL 8.0 (via REST)<br><small>*Note: Phase 3.5 adds PostgreSQL 15 option*</small> | PostgreSQL 15/Aurora | DynamoDB | PostgreSQL 15/Aurora |
| **ORM/Persistence** | EclipseLink | EclipseLink | N/A (REST client) | Hibernate | AWS SDK | Hibernate (Panache) |
| **Application Server** | GlassFish 4.1.1 | GlassFish 4.1.1 | GlassFish 4.1.1 (backend) | Embedded Tomcat | N/A (Lambda) | N/A (Container) |
| **Container Platform** | DevContainer | DevContainer | DevContainer (Node.js 20+) | Docker + ECS/EKS | Lambda Runtime | Kubernetes |
| **Build Tool** | Apache Ant | Apache Ant | Vite 5.0 (frontend), Ant (backend) | Maven/Gradle | Maven/Gradle | Maven/Gradle |
| **JDK Version** | Java 8 | Java 8 | Java 8 (backend), Node.js 20+ (frontend) | Java 17+ | Java 17/21 | Java 17+ |
| **Authentication** | N/A | N/A | N/A | Spring Security | AWS Cognito | OAuth 2.0 |
| **API Documentation** | N/A | rest-test.html | ApiTestPage component | SpringDoc | API Gateway Docs | OpenAPI |
| **Service Discovery** | N/A | N/A | Vite Proxy (dev, no separate service discovery) | N/A | API Gateway | Kubernetes DNS |
| **Messaging** | N/A | N/A | N/A | JMS | EventBridge/SNS | Kafka/EventBridge |
| **Monitoring** | GlassFish Admin | GlassFish Admin | Browser DevTools | Actuator + CloudWatch | CloudWatch | Prometheus + Grafana |
| **CI/CD** | Manual | Manual | Manual (GitHub Actions planned) | GitHub Actions | GitHub Actions | ArgoCD |

## Architecture Pattern Analysis

### Pattern Evolution

```mermaid
graph LR
    subgraph "Monolithic Patterns"
        A[3-Tier Monolith] -.->|Phase 1| B[API-Decoupled Monolith]
    end
    
    subgraph "N-Tier Patterns"
        B -.->|Phase 2-3| C[Client-Server with API Gateway]
    end
    
    subgraph "Cloud Patterns"
        C -.->|Phase 4| D[Containerized Monolith]
        C -.->|Phase 5| E[Serverless Functions]
    end
    
    subgraph "Distributed Patterns"
        D -.->|Phase 6| F[Microservices]
        E -.->|Phase 6| F
    end
    
    style A fill:#ff6b6b
    style B fill:#ffa500
    style C fill:#ffe66d
    style D fill:#95e1d3
    style E fill:#4ecdc4
    style F fill:#6bcb77
```

## Deployment Complexity Comparison

```mermaid
graph LR
    subgraph P1["Phase 1: Legacy"]
        A1[Development Environment] --> A2[Build EAR]
        A2 --> A3[Deploy to GlassFish]
        A3 --> A4[Configure JDBC Pool]
        A4 --> A5[Access via Browser]
    end
    
    subgraph P2["Phase 2: RESTful"]
        B1[Development Environment] --> B2[Build EAR with REST]
        B2 --> B3[Deploy to GlassFish]
        B3 --> B4[Access via Browser]
        B4 --> B5[Test REST API]
    end
    
    subgraph P3["Phase 3: SPA"]
        C1[Frontend: npm build] --> C2[Deploy to S3]
        C2 --> C3[CloudFront Distribution]
        C1 --> C4[Backend: Build EAR]
        C4 --> C5[Deploy to GlassFish]
        C5 --> C6[Configure CORS]
        C3 --> C7[SPA Access]
    end
    
    subgraph P4["Phase 4: Spring Boot"]
        D1[Frontend: npm build] --> D2[Deploy to S3]
        D2 --> D3[CloudFront Distribution]
        D1 --> D4[Backend: Build JAR]
        D4 --> D5[Build Docker Image]
        D5 --> D6[Deploy to ECS/EKS]
        D6 --> D7[Configure ALB]
        D7 --> D8[Test Application]
        D3 --> D8
    end
    
    P1 -.->|Evolution| P2
    P2 -.->|Evolution| P3
    P3 -.->|Evolution| P4
    
    P4 -.->|Evolution| P5
    
    subgraph P5["Phase 5: Serverless"]
        E1[Frontend: npm build] --> E2[Deploy to S3]
        E2 --> E3[CloudFront Distribution]
        E1 --> E4[Backend: Build Lambda ZIPs]
        E4 --> E5[Deploy Lambdas]
        E5 --> E6[API Gateway Setup]
        E6 --> E7[IAM Roles/Permissions]
        E7 --> E8[Test Endpoints]
        E3 --> E8
    end
    
    P5 -.->|Evolution| P6
    
    subgraph P6["Phase 6: Microservices"]
        F1[Frontend: npm build] --> F2[Deploy to S3]
        F2 --> F3[CloudFront Distribution]
        F1 --> F4[Build All Service Images]
        F4 --> F5[Deploy to Kubernetes]
        F5 --> F6[Configure Service Mesh]
        F6 --> F7[Setup API Gateway]
        F7 --> F8[Configure Monitoring]
        F8 --> F9[Test Services]
        F3 --> F9
    end
    
    style A3 fill:#ff6b6b
    style B3 fill:#ff6b6b
    style C5 fill:#ff6b6b
    style D6 fill:#95e1d3
    style E5 fill:#4ecdc4
    style F5 fill:#6bcb77
```

## Scalability Comparison

```mermaid
graph LR
    subgraph "Vertical Scaling"
        A[Phase 1: Legacy]
        B[Phase 2: RESTful]
        C[Phase 3: SPA]
    end
    
    subgraph "Horizontal Scaling"
        D[Phase 4: Spring Boot]
        E[Phase 5: Serverless]
        F[Phase 6: Microservices]
    end
    
    A -->|Limited| B
    B -->|Improved| C
    C -->|Better| D
    D -->|Good| E
    E -->|Excellent| F
    
    style A fill:#ff6b6b
    style B fill:#ff6b6b
    style C fill:#ffe66d
    style D fill:#95e1d3
    style E fill:#4ecdc4
    style F fill:#6bcb77
```

## Performance Characteristics

| Metric | Phase 1: Legacy (hotel-monolith) | Phase 2: RESTful (hotel-api-rest) | Phase 3: SPA (hotel-ui-react) | Phase 4: Spring Boot | Phase 5: Serverless | Phase 6: Microservices |
|--------|----------------|------------------|--------------|---------------------|---------------------|------------------------|
| **Cold Start Time** | N/A (always running) | N/A (always running) | N/A (dev server) | 2-5s | 100-500ms | 100-300ms |
| **Warm Response Time** | 50-100ms | 50-100ms (API) | 100-200ms (with proxy) | 20-50ms | 10-50ms | 10-30ms |
| **Throughput** | Medium | Medium | Medium | High | High | Very High |
| **Latency** | Medium | Medium | Medium (proxy overhead) | Low | Low | Very Low |
| **Concurrent Users** | 100-500 | 100-500 | 500-2000 | 5,000+ | 10,000+ | 20,000+ |
| **Database Connections** | Pool (50) | Pool (50) | Pool (50, via backend) | Pool (200) | N/A (DynamoDB) | Pool per service |
| **Memory Usage** | High (1GB+) | High (1GB+) | Low (frontend only) | Medium (512MB) | Low (128MB+) | Medium (256MB/service) |
| **CPU Usage** | Medium-High | Medium-High | Low (frontend only) | Medium | Low (on-demand) | Low-Medium |
| **Bundle Size** | N/A | N/A | ~200KB (gzipped) | N/A | N/A | N/A |

## Cost Analysis (Estimated Monthly)

| Resource | Phase 1: Legacy (hotel-monolith) | Phase 2: RESTful (hotel-api-rest) | Phase 3: SPA (hotel-ui-react) | Phase 4: Spring Boot | Phase 5: Serverless | Phase 6: Microservices |
|----------|----------------|------------------|-------------|---------------------|---------------------|------------------------|
| **Compute** | EC2: $50-200 | EC2: $50-200 | EC2: $50-200 (backend) | ECS: $30-100 | Lambda: $5-50 | EKS: $150+ |
| **Storage** | EBS: $10 | EBS: $10 | S3: $1 (frontend) | EBS: $20 | S3: $1 | EBS: $50 |
| **Database** | RDS: $50 | RDS: $50 | RDS: $50 (shared backend) | RDS: $50-200 | DynamoDB: $10-50 | RDS: $200-500 |
| **CDN** | N/A | N/A | CloudFront: $10 (frontend) | CloudFront: $10 | CloudFront: $10 | CloudFront: $10 |
| **API Gateway** | N/A | N/A | N/A | N/A | $5-50 | $20-100 |
| **Load Balancer** | ELB: $20 | ELB: $20 | ALB: $25 (backend) | ALB: $25 | N/A | ALB: $50+ |
| **Monitoring** | CloudWatch: $10 | CloudWatch: $10 | CloudWatch: $15 | CloudWatch: $30 | CloudWatch: $20 | CloudWatch + Prometheus: $100+ |
| **Total Monthly** | **$140-290** | **$140-290** | **$150-300** | **$165-365** | **$50-200** | **$580-1000+** |

## Development Experience Comparison

### Setup Time

```
Legacy:         ████████████ 10 minutes (DevContainer)
RESTful:        ████████████ 10 minutes (DevContainer)
SPA:            ████████████████ 15 minutes (npm + backend)
Serverless:     ████████████████████ 30 minutes (SAM/Serverless)
Spring Boot:    ████████████████ 15 minutes (Spring Initializr)
Microservices:  ████████████████████████ 45 minutes (multiple services + K8s)
```

### Testing Complexity

```
Legacy:         ████████ Easy (servlet tests)
RESTful:        ██████████ Moderate (API tests)
SPA:            ████████████ Moderate (E2E tests)
Serverless:     ████████████████ Complex (local testing)
Spring Boot:    ██████████ Moderate (integration tests)
Microservices:  ██████████████████ Very Complex (distributed testing)
```

### Debugging Complexity

```
Legacy:         ██████ Easy (log files)
RESTful:        ██████ Easy (log files)
SPA:            ████████ Moderate (dev tools)
Serverless:     ██████████████ Difficult (CloudWatch logs)
Spring Boot:    ███████ Easy (debugger)
Microservices:  ████████████████ Very Difficult (distributed tracing)
```

## Migration Path Recommendations

### Recommended Paths by Scenario

**Scenario 1: Limited Budget, Low Risk**
```
Phase 1 (Baseline) → Phase 2 (RESTful API) → Phase 3 (SPA Frontend)
```
*Minimal infrastructure changes, gradual UX improvement*

**Scenario 2: Enterprise Standard Stack**
```
Phase 1 (Baseline) → Phase 2 (RESTful API) → Phase 4 (Spring Boot)
```
*Industry-standard, large community, proven patterns*

**Scenario 3: Cloud-Native, Pay-per-Use**
```
Phase 1 (Baseline) → Phase 2 (RESTful API) → Phase 5 (Serverless)
```
*Optimal for variable workloads, minimal operational overhead*

**Scenario 4: Maximum Scalability**
```
Phase 1 (Baseline) → Phase 2 (RESTful API) → Phase 6 (Microservices)
```
*Best for large-scale applications with multiple teams*

**Scenario 5: Complete Modernization**
```
Phase 1 → Phase 2 → Phase 3 → Phase 4 → Phase 5 → Phase 6
```
*Demonstrates full range of architectural patterns (Portfolio)*

## Conclusion

Each phase represents a valid modernization strategy with different trade-offs. The choice depends on:
- **Business objectives** (cost, scalability, developer experience)
- **Team capabilities** (existing skills, learning capacity)
- **Time constraints** (aggressive vs gradual migration)
- **Infrastructure** (existing investments, cloud strategy)
- **Risk tolerance** (incremental vs big-bang approach)

This portfolio demonstrates all paths, providing comprehensive examples of modern software architecture.

