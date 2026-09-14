# FinTech Knowledge Assistant — Spring Boot Service

Backend/business API for the AI-Powered FinTech Knowledge Assistant. This service owns authentication, user & role management, document metadata, document upload, and communication with the Python AI (FastAPI) service for RAG-based question answering.

This is a learning-oriented, production-styled portfolio project.

---

## Architecture Overview

```
                Spring Boot API  ◄──── (this service)
                       |
             +---------+---------+
             |                   |
             v                   v
        PostgreSQL          FastAPI (Python) ──► LLM
             |
             v
          pgvector
```

Spring Boot is the system of record and orchestrator. It never talks to the LLM directly — all AI/RAG functionality is delegated to the FastAPI service over HTTP.

---

## Responsibilities

- Authentication & authorization (JWT access/refresh tokens, MFA, token revocation)
- User management (roles, permissions)
- Document metadata & upload handling
- Proxying AI/RAG requests to the FastAPI service
- Request validation, structured logging, global exception handling
- Transaction management

---

## Tech Stack

| Concern | Technology |
|---|---|
| Language / Framework | Java 25, Spring Boot 3.x |
| Security | Spring Security, JWT (access + refresh tokens) |
| Persistence | Spring Data JPA, PostgreSQL, pgvector |
| Token revocation / cache | Redis |
| Inter-service HTTP | Spring `RestClient` (calls to FastAPI) |
| Build | Maven |

---

## Prerequisites

- Java 21+
- Maven 3.9+
- PostgreSQL 15+ with the `pgvector` extension enabled
- Redis (for refresh/access token revocation)
- The FastAPI AI service running locally (see its own README)
