# Smart Inventory Management System

An AI-powered inventory management system with demand forecasting, multi-warehouse support, and real-time analytics. Built with Spring Boot, React, Python FastAPI, and PostgreSQL.

## Architecture

```
                    +-------------------+
                    |   React Frontend  |
                    |  (Vite + Tailwind)|
                    +--------+----------+
                             |
                    +--------v----------+
                    | Spring Boot API   |
                    | (REST + Security) |
                    +----+--------+-----+
                         |        |
              +----------+        +----------+
              |                              |
    +---------v---------+       +------------v-----------+
    |   PostgreSQL 16   |       |  Python AI Service     |
    |   (Data Store)    |       |  (FastAPI + ML)        |
    +-------------------+       +------------------------+
              |
    +---------v---------+
    |    Redis 7        |
    |    (Cache)        |
    +-------------------+
```

## Features

- **Product Management** - Full CRUD with search, filtering, and pagination
- **Multi-Warehouse Support** - Manage stock across multiple warehouse locations
- **Stock Level Tracking** - Real-time stock monitoring with low-stock alerts
- **Stock Movements** - Track IN, OUT, TRANSFER, and ADJUSTMENT operations
- **Purchase Orders** - Complete workflow: Draft -> Submitted -> Approved -> Received
- **Supplier Management** - Maintain supplier database with search
- **AI Demand Forecasting** - ML-based demand prediction using historical data
- **Reorder Optimization** - EOQ calculations with safety stock recommendations
- **AI Insights** - GPT-4 powered inventory analysis and recommendations
- **Dashboard Analytics** - Stock value charts, movement trends, top products
- **CSV Reports** - Export stock and movement data as CSV
- **JWT Authentication** - Secure API with role-based access
- **Redis Caching** - Performance optimization with configurable TTL

## Tech Stack

| Layer       | Technology                          |
|-------------|-------------------------------------|
| Frontend    | React 18, TypeScript, Vite 5, Tailwind CSS 3 |
| UI Library  | Headless UI, Heroicons, Recharts    |
| Backend     | Java 17, Spring Boot 3.2.2          |
| Security    | Spring Security, JWT (jjwt 0.12.5)  |
| Database    | PostgreSQL 16                       |
| Migrations  | Flyway                              |
| Cache       | Redis 7, Spring Cache               |
| AI Service  | Python 3.11, FastAPI, scikit-learn   |
| AI/ML       | Linear Regression, OpenAI GPT-4     |
| API Docs    | SpringDoc OpenAPI (Swagger)         |
| Deploy      | Docker, Docker Compose              |

## Project Structure

```
smart-inventory/
|-- src/main/java/com/daoninhthai/inventory/
|   |-- config/          # Redis, WebClient configuration
|   |-- controller/      # REST API controllers
|   |-- dto/             # Request/Response DTOs
|   |-- entity/          # JPA entities
|   |-- exception/       # Global exception handling
|   |-- mapper/          # Entity-DTO mappers
|   |-- repository/      # Spring Data JPA repositories
|   |-- security/        # JWT auth, filters, security config
|   |-- service/         # Business logic services
|-- src/main/resources/
|   |-- application.yml
|   |-- db/migration/    # Flyway SQL migrations (V1-V9)
|-- ai-service/
|   |-- models/          # ML models (forecasting, reorder)
|   |-- routes/          # FastAPI route handlers
|   |-- main.py          # FastAPI application
|   |-- schemas.py       # Pydantic models
|-- frontend/
|   |-- src/
|   |   |-- api/         # Axios API clients
|   |   |-- components/  # React components
|   |   |-- context/     # Auth context
|   |   |-- pages/       # Page components
|   |   |-- types/       # TypeScript interfaces
|   |-- vite.config.ts
|   |-- tailwind.config.js
|-- docker-compose.yml
|-- Dockerfile
```

## Getting Started

### Prerequisites

- Java 17+
- Node.js 18+
- Python 3.11+
- PostgreSQL 16
- Redis 7
- Docker & Docker Compose (optional)

### Quick Start with Docker

```bash
# Clone the repository
git clone https://github.com/daoninhthai/smart-inventory.git
cd smart-inventory

# Copy environment file
cp .env.example .env

# Start all services
docker-compose up -d

# Access the application
# Frontend: http://localhost:3000
# Backend API: http://localhost:8080
# AI Service: http://localhost:8000
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### Development Setup

#### 1. Start Infrastructure

```bash
docker-compose -f docker-compose.dev.yml up -d
```

#### 2. Backend (Spring Boot)

```bash
./mvnw spring-boot:run
```

The backend runs on http://localhost:8080

#### 3. AI Service (Python)

```bash
cd ai-service
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt
uvicorn main:app --reload --port 8000
```

The AI service runs on http://localhost:8000

#### 4. Frontend (React)

```bash
cd frontend
npm install
npm run dev
```

The frontend runs on http://localhost:3000

## API Overview

### Authentication
| Method | Endpoint           | Description       |
|--------|--------------------|--------------------|
| POST   | /api/auth/login    | User login         |
| POST   | /api/auth/register | User registration  |

### Products
| Method | Endpoint                | Description          |
|--------|-------------------------|----------------------|
| GET    | /api/products           | List products        |
| GET    | /api/products/{id}      | Get product by ID    |
| GET    | /api/products/search    | Search products      |
| POST   | /api/products           | Create product       |
| PUT    | /api/products/{id}      | Update product       |
| DELETE | /api/products/{id}      | Delete product       |

### Stock Management
| Method | Endpoint                    | Description            |
|--------|-----------------------------|------------------------|
| GET    | /api/stock                  | All stock levels       |
| GET    | /api/stock/alerts           | Low stock alerts       |
| POST   | /api/stock/adjust           | Adjust stock           |
| POST   | /api/stock/transfer         | Transfer between warehouses |

### Purchase Orders
| Method | Endpoint                          | Description       |
|--------|-----------------------------------|-------------------|
| GET    | /api/purchase-orders              | List orders       |
| POST   | /api/purchase-orders              | Create draft      |
| POST   | /api/purchase-orders/{id}/submit  | Submit order      |
| POST   | /api/purchase-orders/{id}/approve | Approve order     |
| POST   | /api/purchase-orders/{id}/receive | Receive order     |
| POST   | /api/purchase-orders/{id}/cancel  | Cancel order      |

### Dashboard & Reports
| Method | Endpoint                    | Description          |
|--------|-----------------------------|----------------------|
| GET    | /api/dashboard/summary      | Dashboard summary    |
| GET    | /api/dashboard/stock-value  | Stock value by warehouse |
| GET    | /api/dashboard/trends       | Stock movement trends |
| GET    | /api/reports/stock          | Stock CSV report     |
| GET    | /api/reports/movements      | Movement CSV report  |

### AI Forecasting
| Method | Endpoint                          | Description            |
|--------|-----------------------------------|------------------------|
| GET    | /api/forecast/demand/{productId}  | Demand forecast        |
| GET    | /api/forecast/reorder/{productId} | Reorder suggestion     |
| POST   | /api/forecast/insights            | AI inventory analysis  |

## Environment Variables

| Variable        | Description                | Default           |
|-----------------|----------------------------|--------------------|
| DB_HOST         | PostgreSQL host            | localhost          |
| DB_PORT         | PostgreSQL port            | 5432               |
| DB_NAME         | Database name              | smart_inventory    |
| DB_USER         | Database username          | postgres           |
| DB_PASS         | Database password          | postgres           |
| REDIS_HOST      | Redis host                 | localhost          |
| JWT_SECRET      | JWT signing secret         | (default key)      |
| OPENAI_API_KEY  | OpenAI API key for insights| (empty)            |
| AI_SERVICE_URL  | AI service base URL        | http://localhost:8000 |

## Author

**daoninhthai** - [GitHub](https://github.com/daoninhthai)

## License

This project is licensed under the MIT License.
