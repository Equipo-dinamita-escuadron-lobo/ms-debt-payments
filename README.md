# Microservicio: Debt Payments (Gestión de Pagos de Cartera)

**Versión:** 0.0.1-SNAPSHOT  
**Grupo:** com.unicauca  
**Artifact ID:** debt-payments  
**Spring Boot:** 3.4.8  
**Java:** 17 LTS  

---

## 📋 Tabla de Contenidos

1. [Descripción General](#descripción-general)
2. [Características Principales](#características-principales)
3. [Requisitos del Sistema](#requisitos-del-sistema)
4. [Instalación y Configuración](#instalación-y-configuración)
5. [Variables de Entorno](#variables-de-entorno)
6. [Estructura del Proyecto](#estructura-del-proyecto)
7. [Arquitectura](#arquitectura)
8. [Endpoints de la API](#endpoints-de-la-api)
9. [Enumeraciones y Estados](#enumeraciones-y-estados)
10. [Integración con Otros Microservicios](#integración-con-otros-microservicios)
11. [Mensajería con RabbitMQ](#mensajería-con-rabbitmq)
12. [Ejecución](#ejecución)
13. [Testing](#testing)
14. [Troubleshooting](#troubleshooting)

---

## 📌 Descripción General

El microservicio **Debt Payments** es responsable de la **gestión integral de cuentas por cobrar y cartera** en el sistema de contabilidad. Este servicio maneja:

- **Pagos y Abonos:** Registro de recibos de caja para pagos parciales o totales de facturas
- **Castigos de Cartera:** Anulación de cuentas por cobrar incobrables (borrador → confirmado → anulado)
- **Gestión de Plazos:** Cambio de fechas de vencimiento de facturas
- **Control de Inventario:** Gestión de estados de recibos y castigos
- **Sincronización Contable:** Envío de información a microservicios de contabilidad y facturación
- **Notificaciones:** Recordatorios automáticos de facturas por vencer

Todo cambio en recibos y castigos tiene impacto directo en la **contabilidad** del sistema, manteniendo la integridad de datos entre microservicios.

---

## ✨ Características Principales

| Características | Descripción |
|---|---|
| **Gestión de Recibos** | Crear, anular y consultar recibos de pago |
| **Castigos de Cartera** | Crear castigos en borrador, confirmarlos o anularlos post-confirmación |
| **Consultas de Facturas** | Buscar facturas pendientes, por vencer o por cliente |
| **Cambio de Plazos** | Modificar fechas de vencimiento de facturas |
| **Autenticación JWT** | Validación de tokens JWT para acceso seguro |
| **Eventos en Tiempo Real** | Publicación de eventos a través de RabbitMQ |
| **Notificaciones Automáticas** | Envío de recordatorios de facturas por vencer |
| **Réplicas de Datos** | Sincronización de datos de facturas desde otros microservicios |
| **Manejo de Errores** | Procesamiento y almacenamiento de errores de mensajes |

---

## 🖥️ Requisitos del Sistema

### Sistema Operativo
- Windows 10/11 (para desarrollo local)
- macOS 12+ (Intel o Apple Silicon con Java 17)
- Linux (Ubuntu 20.04+, CentOS 8+)

### Arquitectura
- x64/AMD64 (requerido para Java 17)

### Software Base
| Componente | Versión | Descripción |
|---|---|---|
| **Java JDK** | 17 LTS | Obligatorio (OpenJDK o Oracle JDK) |
| **Maven** | 3.8+ | Incluido via Maven Wrapper (`mvnw`) |
| **Git** | 2.30+ | Para control de versiones |

### Servicios Externos (para desarrollo completo)
| Servicio | Versión | Propósito |
|---|---|---|
| **PostgreSQL** | 15+ | Base de datos principal |
| **RabbitMQ** | 3.12+ | Mensajería asíncrona |
| **Keycloak** | 20+ | Gestión de identidad y OAuth2 |
| **Eureka Server** | - | Registro y descubrimiento de servicios |

### Recursos del Sistema
- **CPU:** 2+ cores para procesamiento concurrente
- **RAM:** Mínimo 4GB (recomendado 8GB+)
- **Disco:** 1GB+ libres para código, dependencias y datos
- **Red:** Conexión estable para dependencias Maven y servicios externos

---

## 🚀 Instalación y Configuración

### Paso 1: Clonar el Repositorio

```bash
git clone https://github.com/Equipo-dinamita-escuadron-lobo/ms-debt-payments.git
cd debt-payments
```

### Paso 2: Compilar el Proyecto

```bash
# Usar Maven Wrapper (Windows)
mvnw.cmd clean package

# Usar Maven Wrapper (macOS/Linux)
./mvnw clean package
```

### Paso 3: Configurar Variables de Entorno

Crear un archivo `.env` en la raíz del proyecto o configurar en el IDE:

```bash
# Base de Datos PostgreSQL
DB_URL=jdbc:postgresql://localhost:5432/payments
DB_USER=postgres
DB_PASSWORD=root
DB_DRIVER=org.postgresql.Driver
DB_HIBERNATE_DDL_AUTO=create-drop
DB_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USER=guest
RABBITMQ_PASSWORD=guest

# Autenticación JWT (Keycloak)
JWT_ISSUER_URI=http://contables.unicauca.edu.co:80/auth/realms/oauth2-realm
JWT_JWK_SET_URI=http://contables.unicauca.edu.co:80/auth/realms/oauth2-realm/protocol/openid-connect/certs
JWT_PRINCIPAL_ATTR=preferred_username
JWT_RESOURCE_ID=microservices_client

# Eureka Service Registry
EUREKA_URL=http://localhost:8761/eureka/
INSTANCE_HOSTNAME=localhost

# Logging
KEYCLOAK_LOG_LEVEL=DEBUG

# Servidor
PORT=0
PROFILE=dev
```

### Paso 3: Iniciar la Base de Datos PostgreSQL

```bash
# Usando Docker Compose
cd ../DataBase/Postgres/
docker-compose up -d

# Verificar conexión
psql -U postgres -h localhost -d payments
```

### Paso 4: Iniciar RabbitMQ

```bash
# Usando Docker Compose
cd ../RabbitMQ/
docker-compose up -d

# Acceder a Management UI
# http://localhost:15672 (usuario: guest, contraseña: guest)
```

---

## 🔧 Variables de Entorno

### Base de Datos

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/payments}
    username: ${DB_USER:postgres}
    password: ${DB_PASSWORD:root}
    driver-class-name: ${DB_DRIVER:org.postgresql.Driver}
  jpa:
    hibernate:
      ddl-auto: ${DB_HIBERNATE_DDL_AUTO:create-drop}
    properties:
      hibernate:
        dialect: ${DB_HIBERNATE_DIALECT:org.hibernate.dialect.PostgreSQLDialect}
```

**Valores de `ddl-auto`:**
- `create-drop`: Crea la BD en startup y la elimina al cerrar (desarrollo)
- `update`: Actualiza esquema sin borrar datos
- `create`: Crea nueva BD (sobrescribe)
- `validate`: Solo valida que el esquema exista
- `none`: Sin cambios auto

### RabbitMQ

```yaml
spring:
  rabbitmq:
    host: ${RABBITMQ_HOST:localhost}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USER:guest}
    password: ${RABBITMQ_PASSWORD:guest}
    connection-timeout: ${RABBITMQ_CONNECTION_TIMEOUT:30000}
    requested-heartbeat: ${RABBITMQ_HEARTBEAT:30}
    cache:
      connection:
        mode: connection
        size: ${RABBITMQ_CONNECTION_POOL_SIZE:5}
      channel:
        size: ${RABBITMQ_CHANNEL_POOL_SIZE:25}
        checkout-timeout: ${RABBITMQ_CHANNEL_CHECKOUT_TIMEOUT:5000}
    template:
      retry:
        enabled: true
        initial-interval: ${RABBITMQ_RETRY_INITIAL_INTERVAL:1000}
        max-attempts: ${RABBITMQ_RETRY_MAX_ATTEMPTS:3}
        max-interval: ${RABBITMQ_RETRY_MAX_INTERVAL:10000}
        multiplier: ${RABBITMQ_RETRY_MULTIPLIER:2.0}
      mandatory: true
      receive-timeout: ${RABBITMQ_RECEIVE_TIMEOUT:5000}
    listener:
      simple:
        acknowledge-mode: manual
        prefetch: ${RABBITMQ_PREFETCH:10}
        concurrency: ${RABBITMQ_CONCURRENCY:1}
        max-concurrency: ${RABBITMQ_MAX_CONCURRENCY:5}
```

### OAuth2 (JWT)

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${JWT_ISSUER_URI:http://localhost:8090/auth/realms/oauth2-realm}
          jwk-set-uri: ${JWT_JWK_SET_URI:http://localhost:8090/auth/realms/oauth2-realm/protocol/openid-connect/certs}

jwt:
  auth:
    converter:
      principle-attribute: ${JWT_PRINCIPAL_ATTR:preferred_username}
      resource-id: ${JWT_RESOURCE_ID:microservices_client}
```

### Configuración de la Aplicación

```yaml
spring:
  application:
    name: PAYMENTS
  profiles:
    active: ${PROFILE:dev}

server:
  port: ${PORT:0}

eureka:
  instance:
    hostname: ${INSTANCE_HOSTNAME:localhost}
    instance-id: "${spring.application.name}:${random.uuid}"
  client:
    service-url:
      defaultZone: ${EUREKA_URL:http://localhost:8761/eureka/}
```

**Nota:** El puerto `0` significa que se asigna un puerto aleatorio automáticamente.

---

## 📁 Estructura del Proyecto

```
debt-payments/
├── pom.xml                          # Dependencias Maven
├── mvnw / mvnw.cmd                  # Maven Wrapper
├── Dockerfile                       # Configuración Docker
├── docker-compose.yml               # Composición de servicios
├── README.md                        # Este archivo
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── debt_payments/
│   │   │       ├── DebtPaymentsApplication.java
│   │   │       ├── application/        # Casos de uso (Application Layer)
│   │   │       │   ├── input/          # Puertos de entrada (interfaces)
│   │   │       │   │   ├── IReceiptCommandUseCase.java
│   │   │       │   │   ├── IReceiptQueryUseCase.java
│   │   │       │   │   ├── IPortfolioWriteOffCommandUseCase.java
│   │   │       │   │   ├── IPortfolioWriteOffQueryUseCase.java
│   │   │       │   │   ├── IInvoiceCommandUseCase.java
│   │   │       │   │   ├── IInvoiceQueryUseCase.java
│   │   │       │   │   └── ...
│   │   │       │   ├── output/         # Puertos de salida
│   │   │       │   │   ├── IReceiptRepository.java
│   │   │       │   │   ├── IPortfolioWriteOffRepository.java
│   │   │       │   │   └── ...
│   │   │       │   └── service/        # Implementación de casos de uso
│   │   │       ├── domain/             # Lógica de negocio (Domain Layer)
│   │   │       │   ├── model/          # Entidades del dominio
│   │   │       │   │   ├── Receipt.java
│   │   │       │   │   ├── ReceiptDetail.java
│   │   │       │   │   ├── PortfolioWriteOff.java
│   │   │       │   │   ├── WriteOffDetail.java
│   │   │       │   │   ├── Replica/
│   │   │       │   │   ├── used/
│   │   │       │   │   └── ...
│   │   │       │   ├── enums/          # Enumeraciones
│   │   │       │   │   ├── ReceiptStatus.java
│   │   │       │   │   ├── ReceiptType.java
│   │   │       │   │   ├── WriteOffStatus.java
│   │   │       │   │   └── InvoiceStatus.java
│   │   │       │   ├── exception/      # Excepciones personalizadas
│   │   │       │   └── ports/          # Interfaces de puertos
│   │   │       └── infraestructure/    # Implementación técnica (Infrastructure Layer)
│   │   │           ├── input/          # Adaptadores de entrada (REST, etc.)
│   │   │           │   ├── rest/
│   │   │           │   │   ├── controller/
│   │   │           │   │   │   ├── ReceiptController.java
│   │   │           │   │   │   ├── PortfolioWriteOffController.java
│   │   │           │   │   │   ├── InvoiceController.java
│   │   │           │   │   │   └── ...
│   │   │           │   │   ├── dto/
│   │   │           │   │   │   ├── request/
│   │   │           │   │   │   │   ├── ReceiptCreateRequest.java
│   │   │           │   │   │   │   ├── ReceiptDetailRequest.java
│   │   │           │   │   │   │   ├── VoidReceiptRequest.java
│   │   │           │   │   │   │   ├── CreateWriteOffRequest.java
│   │   │           │   │   │   │   ├── WriteOffDetailRequest.java
│   │   │           │   │   │   │   ├── UpdateDueDateRequest.java
│   │   │           │   │   │   │   └── ...
│   │   │           │   │   │   └── response/
│   │   │           │   │   │       ├── ApiResponse.java
│   │   │           │   │   │       ├── ReceiptResponse.java
│   │   │           │   │   │       ├── PortfolioWriteOffResponse.java
│   │   │           │   │   │       ├── InvoicePendingResponse.java
│   │   │           │   │   │       └── ...
│   │   │           │   │   └── mapper/
│   │   │           │   │       ├── IReceiptRestMapper.java
│   │   │           │   │       ├── IPortfolioWriteOffRestMapper.java
│   │   │           │   │       └── ...
│   │   │           │   └── scheduler/  # Tareas programadas
│   │   │           ├── output/         # Adaptadores de salida (DB, Mensajería, etc.)
│   │   │           │   ├── repository/ # Acceso a datos JPA
│   │   │           │   ├── persistence/
│   │   │           │   ├── event/      # Publicadores de eventos
│   │   │           │   └── ...
│   │   │           └── config/
│   │   │               ├── SecurityConfig.java
│   │   │               ├── CorsConfig.java
│   │   │               ├── RabbitMQConfig.java
│   │   │               └── ...
│   │   └── resources/
│   │       ├── application.yml         # Configuración por defecto
│   │       ├── application-dev.yml     # Configuración desarrollo
│   │       ├── application-test.properties # Configuración pruebas
│   │       └── messages/               # Archivos de propiedades i18n
│   └── test/                          # Tests unitarios e integración
│       └── java/
│           └── debt_payments/
│               ├── application/
│               ├── infraestructure/
│               └── domain/
└── target/
    ├── classes/
    ├── test-classes/
    ├── generated-sources/
    ├── generated-test-sources/
    └── surefire-reports/
```

---

## 🏛️ Arquitectura

Este microservicio implementa **Arquitectura Hexagonal (Ports & Adapters)** desacoplando completamente la lógica de negocio de las dependencias externas.

### Capas de la Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│ PRESENTATION LAYER (Entrada)                                │
│ REST Controllers, DTOs, Mappers                              │
└────────────────────┬────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────┐
│ APPLICATION LAYER (Casos de Uso)                             │
│ Interfaces (Puertos) - Orchestración de lógica               │
└────────────────────┬────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────┐
│ DOMAIN LAYER (Lógica de Negocio)                             │
│ Entidades, Enumeraciones, Excepciones, Reglas de Negocio     │
└────────────────────┬────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────┐
│ INFRASTRUCTURE LAYER (Salida)                                │
│ Repositorios, Eventos, Configuración, Persistencia          │
└─────────────────────────────────────────────────────────────┘
```

### Patrones Utilizados

- **CQRS:** Separación de comandos (CREATE, UPDATE, DELETE) y consultas (READ)
- **Event Sourcing:** Publicación de eventos a través de RabbitMQ
- **Mapper Pattern:** MapStruct para conversión de DTOs
- **Repository Pattern:** Abstracción del acceso a datos
- **Template method:** Estandarizar el procesamiento de mensajes asincronos
- **Strategy:** Mantener desacoplado el dominio de la infraestrutura

---

## 📡 Endpoints de la API

### Estructura General de Respuestas

Todas las respuestas siguen el patrón **ApiResponse**:

```json
{
  "success": true,
  "message": "Descripción de la operación",
  "code": "OK",
  "data": { /* Objeto con datos */ },
  "status": null,
  "path": null
}
```

Para errores:
```json
{
  "success": false,
  "message": "Descripción del error",
  "code": "ERROR_CODE",
  "data": null,
  "status": 400,
  "path": "/api/payments/..."
}
```

---

### 1️⃣ RECIBOS (Receipts) - `/api/payments`

#### 🟢 POST: Crear Recibo

**Endpoint:** `POST http://localhost:8080/api/payments/`

**Descripción:** Crear un nuevo recibo de pago para una factura. El microservicio soporta dos tipos de recibos:
1. **Abono a Deuda (receiptTypeId: 1)** - Pago parcial o total de facturas existentes
2. **Ingreso Directo (receiptTypeId: 2)** - Ingreso sin asociación a facturas

**Autenticación:** JWT (Bearer Token)

**Request Body - Ejemplo 1: Abono a Deuda**
```json
{
  "thirdPartyId": 1,
  "paymentMethodId": 5,
  "paymentMethodAccount": 11050101,
  "receiptTypeId": 1,
  "observations": "Abono a factura FACT-001",
  "enterpriseId": "{{enterpriseId}}",
  "totalAmount": 2000,
  "details": [
    {
      "invoiceId": 1,
      "amountPaid": 10000
    }
  ]
}
```

**Request Body - Ejemplo 2: Ingreso Directo**
```json
{
  "thirdPartyId": 1,
  "paymentMethodId": 1,
  "paymentMethodAccount": 11050101,
  "receiptTypeId": 2,
  "observations": "Ingreso directo de prueba",
  "ledgerAccountId": 13050513,
  "centerCostId": 1,
  "enterpriseId": "{{enterpriseId}}",
  "totalAmount": 2000,
  "details": []
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Recibo creado exitosamente.",
  "code": "OK",
  "data": {
    "id": 1,
    "receiptCode": "REC-2026-00001",
    "thirdPartyId": 123,
    "paymentMethodId": 1,
    "paymentMethodAccount": 5001,
    "enterpriseId": "EMP-001",
    "receiptTypeId": 1,
    "status": "CREATED",
    "issueDate": "2026-04-07",
    "totalAmount": 500000,
    "observations": "Pago parcial del cliente",
    "ledgerAccountId": 1000,
    "details": [
      {
        "invoiceId": 100,
        "amountPaid": 250000
      },
      {
        "invoiceId": 101,
        "amountPaid": 250000
      }
    ]
  }
}
```

**Códigos de Respuesta:**
- `201 Created` - Recibo creado exitosamente
- `400 Bad Request` - Datos inválidos
- `401 Unauthorized` - Token JWT inválido/expirado
- `422 Unprocessable Entity` - Validación fallida

---

#### 🟡 PUT: Anular Recibo

**Endpoint:** `PUT http://localhost:8080/api/payments/{id}/void`

**Descripción:** Anular un recibo existente (solo recibos en estado CREATED). Esto reversa el impacto contable y anula el pago.

**Autenticación:** JWT (Bearer Token)

**Path Parameters:**
| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | Long | ID del recibo a anular (ejemplo: 2) |

**Request Body:**
```json
{
  "reason": "Prueba de anulación para asiento contable"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Recibo anulado correctamente.",
  "code": "OK",
  "data": {
    "id": 1,
    "receiptCode": "REC-2026-00001",
    "thirdPartyId": 123,
    "status": "VOIDED",
    "issueDate": "2026-04-07",
    "totalAmount": 500000,
    "voidDate": "2026-04-07",
    "voidReasonDescription": "Recibo anulado por error en el cálculo del monto",
    "details": []
  }
}
```

**Códigos de Respuesta:**
- `200 OK` - Recibo anulado exitosamente
- `404 Not Found` - Recibo no encontrado
- `409 Conflict` - Recibo no puede ser anulado (estado inválido)
- `400 Bad Request` - Razón vacía o inválida

---

#### 🔵 GET: Obtener Recibo por ID

**Endpoint:** `GET /api/payments/{id}`

**Descripción:** Recuperar detalles de un recibo específico

**Autenticación:** JWT

**Path Parameters:**
| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | Long | ID del recibo |

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Operación exitosa.",
  "code": "OK",
  "data": {
    "id": 1,
    "receiptCode": "REC-2026-00001",
    "thirdPartyId": 123,
    "paymentMethodId": 1,
    "enterpriseId": "EMP-001",
    "status": "CREATED",
    "issueDate": "2026-04-07",
    "totalAmount": 500000,
    "details": [...]
  }
}
```

---

#### 🔵 GET: Obtener Recibos por Empresa

**Endpoint:** `GET /api/payments/by-enterprise/{enterpriseId}`

**Descripción:** Listar todos los recibos de una empresa

**Autenticación:** JWT

**Path Parameters:**
| Parámetro | Tipo | Descripción |
|---|---|---|
| `enterpriseId` | String | ID de la empresa |

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Operación exitosa.",
  "code": "OK",
  "data": [
    {
      "id": 1,
      "receiptCode": "REC-2026-00001",
      "enterpriseId": "EMP-001",
      "status": "CREATED",
      "totalAmount": 500000,
      ...
    },
    {
      "id": 2,
      "receiptCode": "REC-2026-00002",
      "enterpriseId": "EMP-001",
      "status": "VOIDED",
      "totalAmount": 250000,
      ...
    }
  ]
}
```

**Response (200 OK - Vacío):**
```json
{
  "success": true,
  "message": "No se encontraron recibos para la empresa especificada.",
  "code": "NO_CONTENT",
  "data": null
}
```

---

#### 🔵 GET: Obtener Recibos por Factura

**Endpoint:** `GET /api/payments/by-invoice/{invoiceId}`

**Descripción:** Listar todos los recibos aplicados a una factura específica

**Autenticación:** JWT

**Path Parameters:**
| Parámetro | Tipo | Descripción |
|---|---|---|
| `invoiceId` | String | ID de la factura |

**Response (200 OK):**
Similar a endpoint anterior (lista de recibos)

---

#### 🔵 GET: Obtener Recibos por Tercero

**Endpoint:** `GET /api/payments/by-third/{thirdId}/{enterpriseId}`

**Descripción:** Listar todos los recibos de un tercero en una empresa

**Autenticación:** JWT

**Path Parameters:**
| Parámetro | Tipo | Descripción |
|---|---|---|
| `thirdId` | String | ID del tercero/cliente |
| `enterpriseId` | String | ID de la empresa |

**Response (200 OK):**
Similar a endpoint anterior (lista de recibos)

---

### 2️⃣ CASTIGOS DE CARTERA (Write-offs) - `/api/payments/write-offs`

#### 🟢 POST: Crear Castigo

**Endpoint:** `POST http://localhost:8080/api/payments/write-offs/`

**Descripción:** Crear un nuevo castigo de cartera en estado DRAFT. El castigo debe ser confirmado posteriormente para impactar la contabilidad.

**Autenticación:** JWT

**Request Body:**
```json
{
  "justification": "Prueba para guardar centro de costo, anterior se me olvido poner el atributo.",
  "writeOffDate": "2026-05-01",
  "debitAuxiliaryAccount": 11050102,
  "debitAuxiliaryAccountId": 6,
  "thirdId": 2,
  "enterpriseId": "{{enterpriseId}}",
  "costCenterId": 1,
  "details": [
    {
      "invoiceId": 1
    }
  ]
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Castigo de cartera creado exitosamente.",
  "code": "OK",
  "data": {
    "id": 1,
    "code": "WOF-2026-00001",
    "justification": "Castigo por cliente insolvente desde hace 12 meses",
    "totalAmount": 500000,
    "writeOffDate": "2026-04-07",
    "debitAuxiliaryAccount": 5105,
    "debitAuxiliaryAccountId": 25,
    "thirdId": 123,
    "costCenterId": 10,
    "status": "DRAFT",
    "enterpriseId": "EMP-001",
    "details": [
      {
        "invoiceId": 100,
        "amountWrittenOff": 250000,
        "accountingAccount": "1305"
      },
      {
        "invoiceId": 101,
        "amountWrittenOff": 250000,
        "accountingAccount": "1305"
      }
    ]
  }
}
```

**Validaciones:**
- La fecha de castigo no puede ser en el pasado
- Todas las facturas deben existir
- La justificación no puede exceder 500 caracteres
- Al menos una factura debe ser incluida

---

#### 🟡 PUT: Confirmar Castigo

**Endpoint:** `PUT /api/payments/write-offs/{id}/confirm`

**Descripción:** Confirmar un castigo (transición DRAFT → CONFIRMED)

**Autenticación:** JWT

**Path Parameters:**
| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | Long | ID del castigo |

**Request Body:** (vacío)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Castigo de cartera confirmado.",
  "code": "OK",
  "data": {
    "id": 1,
    "code": "WOF-2026-00001",
    "status": "CONFIRMED",
    "details": [...]
  }
}
```

**Impactos:**
- Se envía evento a microservicio de contabilidad
- Se actualiza la información en microservicios dependientes

---

#### 🟡 PUT: Anular Castigo Confirmado

**Endpoint:** `PUT /api/payments/write-offs/{id}/void`

**Descripción:** Anular la confirmación de un castigo (CONFIRMED → VOIDED)

**Autenticación:** JWT

**Path Parameters:**
| Parámetro | Tipo | Descripción |
|---|---|---|
| `id` | Long | ID del castigo |

**Request Body:** (vacío)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Confirmación de castigo de cartera anulada.",
  "code": "OK",
  "data": {
    "id": 1,
    "code": "WOF-2026-00001",
    "status": "VOIDED",
    "details": [...]
  }
}
```

**Nota:** Solo se puede anular un castigo CONFIRMED, no un DRAFT

---

#### 🔵 GET: Obtener Castigo por ID

**Endpoint:** `GET /api/payments/write-offs/{id}`

**Descripción:** Recuperar detalles de un castigo específico

**Autenticación:** JWT

**Response (200 OK):** Similar a respuesta de creación

---

#### 🔵 GET: Obtener Castigos por Empresa

**Endpoint:** `GET /api/payments/write-offs/by-enterprise/{enterpriseId}`

**Descripción:** Listar todos los castigos de una empresa

**Autenticación:** JWT

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Operación exitosa.",
  "code": "OK",
  "data": [
    {
      "id": 1,
      "code": "WOF-2026-00001",
      "status": "DRAFT",
      "totalAmount": 500000,
      ...
    }
  ]
}
```

---

#### 🔵 GET: Obtener Castigos Confirmados o Anulados

**Endpoint:** `GET /api/payments/write-offs/confirmed-or-voided/by-enterprise/{enterpriseId}`

**Descripción:** Listar solo castigos que han impactado contabilidad (CONFIRMED o VOIDED)

**Autenticación:** JWT

**Response (200 OK):** Similar al endpoint anterior

---

### 3️⃣ FACTURAS (Invoices) - `/api/payments`

#### 🔵 GET: Obtener Factura por ID

**Endpoint:** `GET /api/payments/invoices/{invoiceId}`

**Descripción:** Obtener detalles de una factura específica

**Autenticación:** JWT

**Path Parameters:**
| Parámetro | Tipo | Descripción |
|---|---|---|
| `invoiceId` | Long | ID de la factura |

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Operación exitosa.",
  "code": "OK",
  "data": {
    "id": 100,
    "factCode": "FACT-2026-001",
    "pendingValue": 500000,
    "thirdId": 123,
    "totalValue": 500000,
    "creationDate": "2026-03-01",
    "expirationDate": "2026-05-01"
  }
}
```

---

#### 🔵 GET: Obtener Facturas Pendientes por Cliente

**Endpoint:** `GET /api/payments/pending/client/{clientId}/enterprise/{enterpriseId}`

**Descripción:** Listar todas las facturas pendientes de un cliente

**Autenticación:** JWT

**Path Parameters:**
| Parámetro | Tipo | Descripción |
|---|---|---|
| `clientId` | Long | ID del cliente |
| `enterpriseId` | String | ID de la empresa |

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Operación exitosa.",
  "code": "OK",
  "data": [
    {
      "id": 100,
      "factCode": "FACT-2026-001",
      "pendingValue": 250000,
      "thirdId": 123,
      "expirationDate": "2026-05-01"
    },
    {
      "id": 101,
      "factCode": "FACT-2026-002",
      "pendingValue": 250000,
      "thirdId": 123,
      "expirationDate": "2026-05-15"
    }
  ]
}
```

---

#### 🔵 GET: Obtener Todas las Facturas por Empresa

**Endpoint:** `GET /api/payments/invoices/by-enterprise/{enterpriseId}`

**Descripción:** Listar todas las facturas de una empresa

**Autenticación:** JWT

**Response (200 OK):** Similar al anterior

---

#### 🔵 GET: Obtener Facturas Pendientes por Empresa

**Endpoint:** `GET /api/payments/invoices/pending/by-enterprise/{enterpriseId}`

**Descripción:** Listar solo facturas pendientes de una empresa

**Autenticación:** JWT

**Response (200 OK):** Similar al anterior

---

#### 🔵 GET: Obtener Facturas por Estado y Cliente

**Endpoint:** `GET /api/payments/invoices/status/by-client/{clientId}/{status}/{enterpriseId}`

**Descripción:** Listar facturas con un estado específico de un cliente

**Autenticación:** JWT

**Path Parameters:**
| Parámetro | Tipo | Descripción |
|---|---|---|
| `clientId` | Long | ID del cliente |
| `status` | Enum | PENDING, PAID, PARTIAL, WRITTEN_OFF |
| `enterpriseId` | String | ID de la empresa |

**Response (200 OK):** Similar a listados anteriores

---

#### 🔵 GET: Obtener Facturas por Vencer

**Endpoint:** `GET /api/payments/expiring?enterpriseId={enterpriseId}&days={days}`

**Descripción:** Listar facturas que vencen en los próximos N días

**Autenticación:** JWT

**Query Parameters:**
| Parámetro | Tipo | Requerido | Descripción |
|---|---|---|---|
| `enterpriseId` | String | ✅ | ID de la empresa |
| `days` | Integer | ❌ | Días a futuro (default: 5) |

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Operación exitosa.",
  "code": "OK",
  "data": [
    {
      "id": 100,
      "factCode": "FACT-2026-001",
      "pendingValue": 300000,
      "expirationDate": "2026-04-10"
    }
  ]
}
```

---

#### 🟡 PATCH: Actualizar Fecha de Vencimiento

**Endpoint:** `PATCH http://localhost:8080/api/payments/{invoiceId}/due-date`

**Descripción:** Cambiar la fecha de vencimiento de una factura. Permite modificar los plazos de pago acordados.

**Autenticación:** JWT

**Path Parameters:**
| Parámetro | Tipo | Descripción |
|---|---|---|
| `invoiceId` | Long | ID de la factura (ejemplo: 1) |

**Request Body:**
```json
{
  "newDueDate": "{{newDueDate}}"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "La fecha de vencimiento de la factura ha sido actualizada correctamente.",
  "code": "OK",
  "data": null
}
```

**Validaciones:**
- La nueva fecha no puede ser en el pasado
- La factura debe existir

---

#### 🟢 POST: Disparar Recordatorios de Facturas

**Endpoint:** `POST /api/payments/trigger-reminders`

**Descripción:** Iniciar proceso de envío de recordatorios de facturas por vencer (integra con ms_notifications)

**Autenticación:** JWT

**Request Body:** (vacío)

**Response (200 OK):**
```json
"Proceso de envío de recordatorios iniciado. Revisa los logs de ms_debt_payments y ms_notifications para ver el progreso."
```

**Error (500):**
```json
"Error al iniciar el proceso de recordatorios: {detalles del error}"
```

---

## 🎯 Enumeraciones y Estados

### Receipt Status (Estado de Recibos)

```java
enum ReceiptStatus {
    CREATED,    // Recibo creado, puede ser anulado
    VOIDED      // Recibo anulado
}
```

| Estado | Descripción | Transiciones |
|---|---|---|
| `CREATED` | Recibo acaba de ser registrado | → VOIDED |
| `VOIDED` | Recibo ha sido anulado | (terminal) |

---

### Receipt Type (Tipo de Recibo)

El sistema soporta dos tipos principales de recibos:

```java
enum ReceiptType {
    INVOICE_PAYMENT,    // ID: 1 - Abono a Deuda (pago de facturas existentes)
    ADVANCE             // ID: 2 - Ingreso Directo (sin asociación a facturas)
}
```

| Tipo | ID | Descripción | Uso | Field `details` |
|---|---|---|---|---|
| **Abono a Deuda** | 1 | Pago parcial o total de facturas existentes | Más común en gestión de cartera | ✅ Requerido (min 1 factura) |
| **Ingreso Directo** | 2 | Ingreso sin asociación directa a facturas, usado para anticipos o depósitos | Gestión de fondos | ✅ Vacío (array vacío) |

**Nota:** Ambos tipos requieren:
- `thirdPartyId` (Cliente/Tercero)
- `paymentMethodId` (Forma de pago)
- `paymentMethodAccount` (Cuenta del método de pago)

---

### Write-Off Status (Estado de Castigos)

```java
enum WriteOffStatus {
    DRAFT,       // Castigo en borrador, puede ser editado o confirmado
    CONFIRMED,   // Castigo confirmado, ha impactado contabilidad
    VOIDED       // Castigo confirmado pero anulado después
}
```

| Estado | Descripción | Transiciones | Impacto Contable |
|---|---|---|---|
| `DRAFT` | Castigo creado pero no confirmado | → CONFIRMED | No |
| `CONFIRMED` | Castigo confirmado | → VOIDED | Sí (se contabiliza) |
| `VOIDED` | Castigo confirmado pero anulado | (terminal) | Sí (se reversa) |

---

### Invoice Status (Estado de Facturas)

```java
enum InvoiceStatus {
    PENDING,        // Factura sin pagar
    PAID,           // Factura pagada en su totalidad
    PARTIAL,        // Factura con pagos pero saldo pendiente
    WRITTEN_OFF,    // Factura castigada
    CANCELLED       // Factura cancelada
}
```

---

## 🔗 Integración con Otros Microservicios

### 1. **Microservicio de Facturación** (`facture-management`)

**Comunicación:** Síncrona (REST) + Asíncrona (RabbitMQ)

**Información que precisa:**
- Detalles de facturas (monto, estado, cliente)
- Cuentas contables asociadas a facturas

**API utilizada:**
```
GET /api/facture-management/invoices/{id}
GET /api/facture-management/invoices/by-ids
```

**Eventos que recibe (RabbitMQ):**

Debt Payments consume eventos de **creación de facturas** desde facture-management:

Event Type: `SALE` (Factura de Venta)

**Estructura del Evento Recibido:**
```json
{
  "properties": {
    "content_type": "application/json",
    "headers": {
      "x-jwt-token": "{{tokenKeycloak}}"
    }
  },
  "routing_key": "",
  "payload": "{\"type\":\"SALE\",\"data\":{\"factCode\":2024001,\"entId\":\"{{enterpriseId}}\",\"thirdId\":456,\"totalValue\":200000,\"totalPay\":50000,\"pendingValue\":150000,\"creationDate\":\"2024-07-01\",\"expirationDate\":\"2025-07-15\",\"active\":true,\"accountingAccount\":130505}}",
  "payload_encoding": "string"
}
```

**Datos Parseados del Payload:**
```json
{
  "type": "SALE",
  "data": {
    "factCode": 2024001,
    "entId": "EMP-001",
    "thirdId": 456,
    "totalValue": 200000,
    "totalPay": 50000,
    "pendingValue": 150000,
    "creationDate": "2024-07-01",
    "expirationDate": "2025-07-15",
    "active": true,
    "accountingAccount": 130505
  }
}
```

**Uso en debt-payments:**
- Se sincroniza una **réplica de la factura** en BD local
- Se utiliza para validar facturas en operaciones de pagos y castigos
- Se actualiza cuando hay cambios en la factura desde facturación

---

### 2. **Microservicio de Contabilidad** (`accounting-service`)

**Comunicación:** Asíncrona (RabbitMQ)

**Eventos enviados:**
- `ReceiptCreated` - Cuando se crea un recibo (impacta contabilidad)
- `ReceiptVoided` - Cuando se anula un recibo (reversa contabilidad)
- `WriteOffConfirmed` - Cuando se confirma un castigo (registra castigo)
- `WriteOffVoided` - Cuando se anula un castigo (reversa castigo)

---

### 3. **Microservicio de Configuración** (`ms-configuration`)

**Comunicación:** Asíncrona (RabbitMQ)

**Eventos enviados:**

Cuando se usan los siguientes recursos, se envía evento al microservicio de configuración para marcarlos como "en uso":

- `PaymentMethodUsed` - Método de pago utilizado en recibo
- `ThirdPartyUsed` - Tercero/Cliente utilizado en recibo o castigo
- `CostCenterUsed` - Centro de costo utilizado

**Propósito:** Prevenir eliminación de datos maestros que están en uso

---

### 4. **Microservicio de Notificaciones** (`ms_notifications`)

**Comunicación:** Asíncrona (RabbitMQ)

**Eventos enviados:**
- `InvoiceDueNotification` - Recordatorios de facturas por vencer

---

## 📨 Mensajería con RabbitMQ

### Exchanges y Queues Configurados

#### Exchange: `payments.events`
**Tipo:** Direct Exchange  
**Propósito:** Publicación de eventos de negocio

| Queue | Routing Key | Consumer |
|---|---|---|
| `payments.receipt.created` | `receipt.created` | Accounting |
| `payments.receipt.voided` | `receipt.voided` | Accounting |
| `payments.writeoff.confirmed` | `writeoff.confirmed` | Accounting |
| `payments.writeoff.voided` | `writeoff.voided` | Accounting |
| `payments.resource.used` | `resource.used` | Configuration |
| `payments.invoice.due` | `invoice.due` | Notifications |

---

### Formato de Eventos

#### Evento: ReceiptCreated

```json
{
  "eventId": "evt-12345",
  "eventType": "ReceiptCreated",
  "timestamp": "2026-04-07T10:30:00Z",
  "receiptId": 1,
  "receiptCode": "REC-2026-00001",
  "enterpriseId": "EMP-001",
  "thirdPartyId": 123,
  "totalAmount": 500000,
  "paymentMethodId": 1,
  "details": [
    {
      "invoiceId": 100,
      "amountPaid": 250000
    }
  ]
}
```

#### Evento: WriteOffConfirmed

```json
{
  "eventId": "evt-12345",
  "eventType": "WriteOffConfirmed",
  "timestamp": "2026-04-07T10:30:00Z",
  "writeOffId": 1,
  "writeOffCode": "WOF-2026-00001",
  "enterpriseId": "EMP-001",
  "thirdPartyId": 123,
  "totalAmount": 500000,
  "details": [
    {
      "invoiceId": 100,
      "amountWrittenOff": 250000,
      "accountingAccount": "1305"
    }
  ]
}
```

#### Evento: PaymentMethodUsed

```json
{
  "eventId": "evt-12345",
  "eventType": "ResourceUsed",
  "resourceType": "PaymentMethod",
  "resourceId": 1,
  "enterpriseId": "EMP-001"
}
```

---

### Configuración de Retry

```yaml
Retry Policy:
  - Intentos: 3
  - Intervalo inicial: 1000ms
  - Intervalo máximo: 10000ms
  - Multiplicador: 2.0
  
Ejemplo: 1s → 2s → 4s
```

---

### Configuración de Connection Pool

```yaml
Connection Pool:
  - Tamaño: 5 conexiones
  - Modo: Connection pooling
  
Channel Pool:
  - Tamaño: 25 canales
  - Prefetch: 10 mensajes
  - Concurrencia: 1-5 workers
```

---

## 🚀 Ejecución

### Opción 1: Ejecución Local en VS Code

**Requisitos Previos:**
1. Java 17 JDK instalado
2. PostgreSQL ejecutándose en `localhost:5432`
3. RabbitMQ ejecutándose en `localhost:5672`
4. Eureka Server ejecutándose en `http://localhost:8761`
5. Keycloak ejecutándose en `http://contables.unicauca.edu.co:80` (producción)

**Pasos:**

1. Click en **Run and Debug** (Ctrl + Shift + D)

2. Seleccionar la configuración: **Spring Boot-DebtPaymentsApplication<debt-payments>**

3. Click en "Start" (play icon)

4. Esperar a que termine la compilación

5. En la consola aparecerá:
```
2026-04-07 10:30:00.000  INFO  [...] - Started DebtPaymentsApplication
2026-04-07 10:30:00.000  INFO  [...] - Registering service PAYMENTS in Eureka
```

6. El microservicio está listo. El puerto se asigna aleatoriamente y sale en logs.

**Para obtener el puerto:**
```
Buscar en los logs: *** - Started DebtPaymentsApplication in X.XXX seconds (JVM running for Y.YYY)
o revisar:
http://localhost:8761/eureka/ → buscar "PAYMENTS"
```

---

### Opción 2: Ejecución con Docker

```bash
# Crear imagen Docker
docker build -t debt-payments:0.0.1 .

# Ejecutar contenedor
docker run -d \
  -e DB_URL=jdbc:postgresql://postgres:5432/payments \
  -e DB_USER=postgres \
  -e DB_PASSWORD=root \
  -e RABBITMQ_HOST=rabbitmq \
  -e JWT_ISSUER_URI=http://keycloak:8080/auth/realms/oauth2-realm \
  -e JWT_JWK_SET_URI=http://keycloak:8080/auth/realms/oauth2-realm/protocol/openid-connect/certs \
  -e EUREKA_URL=http://eureka:8761/eureka/ \
  -p 8080:8080 \
  --name debt-payments \
  --network contapp-network \
  debt-payments:0.0.1
```

---

### Opción 3: Docker Compose (Stack Completo)

**Crear `docker-compose.yml` en la raíz del Backend:**

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: payments
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: root
    ports:
      - "5432:5432"
    networks:
      - contapp-network

  rabbitmq:
    image: rabbitmq:3.12-management
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest
    ports:
      - "5672:5672"
      - "15672:15672"
    networks:
      - contapp-network

  eureka:
    image: springcloud/eureka:2029.0.2
    ports:
      - "8761:8761"
    networks:
      - contapp-network

  debt-payments:
    build: ./debt-payments
    depends_on:
      - postgres
      - rabbitmq
      - eureka
    environment:
      DB_URL: jdbc:postgresql://postgres:5432/payments
      DB_USER: postgres
      DB_PASSWORD: root
      RABBITMQ_HOST: rabbitmq
      EUREKA_URL: http://eureka:8761/eureka/
    ports:
      - "0:8080"
    networks:
      - contapp-network

networks:
  contapp-network:
    driver: bridge
```

**Ejecutar:**
```bash
docker-compose up -d
```

---

## 🧪 Testing

### Estructura de Tests

```
src/test/java/debt_payments/
├── application/
│   ├── service/
│   │   ├── ReceiptServiceTest.java
│   │   ├── PortfolioWriteOffServiceTest.java
│   │   └── ...
│   └── usecase/
│       └── ...
├── infraestructure/
│   ├── input/
│   │   ├── rest/
│   │   │   ├── controller/
│   │   │   │   ├── ReceiptControllerTest.java
│   │   │   │   ├── PortfolioWriteOffControllerTest.java
│   │   │   │   └── ...
│   │   │   └── mapper/
│   │   │       ├── IReceiptRestMapperUnitTest.java
│   │   │       └── ...
│   │   └── ...
│   ├── output/
│   │   ├── repository/
│   │   │   ├── ReceiptRepositoryTest.java
│   │   │   └── ...
│   │   └── ...
│   └── ...
└── domain/
    ├── model/
    │   ├── ReceiptTest.java
    │   ├── PortfolioWriteOffTest.java
    │   └── ...
    └── ...
```

### Ejecución de Tests

**Ejecutar todos los tests:**
```bash
mvnw.cmd test
```

**Ejecutar tests de una clase específica:**
```bash
mvnw.cmd test -Dtest=ReceiptControllerTest
```

**Ejecutar tests con cobertura:**
```bash
mvnw.cmd test jacoco:report
# Reporte en: target/site/jacoco/index.html
```

**Ejecutar solo tests unitarios:**
```bash
mvnw.cmd test -DexcludedGroups=integration
```

**Ejecutar solo tests de integración:**
```bash
mvnw.cmd test -DincludedGroups=integration
```

---

### Colecciones Postman

**Folders:**
- Payments / Receipts
- Payments / Write-offs
- Payments / Invoices

**Variables de entorno a configurar:**
```
{{base_url}} - URL del microservicio (ej: http://localhost:8080)
{{jwt_token}} - Token JWT válido
{{enterprise_id}} - ID de empresa (ej: EMP-001)
{{client_id}} - ID de cliente
{{invoice_id}} - ID de factura
{{receipt_id}} - ID de recibo
```

---

## 🔍 Troubleshooting

### Problema: Connection refused a PostgreSQL

**Solución:**
```bash
# Verificar que PostgreSQL está running
psql -U postgres -h localhost

# Si no está disponible:
cd DataBase/Postgres/
docker-compose up -d

# Verificar estado
docker ps | grep postgres
```

---

### Problema: RabbitMQ connection timeout

**Síntoma:** En logs aparece `java.net.ConnectException: Connection refused`

**Solución:**
```bash
# Verificar RabbitMQ
docker ps | grep rabbitmq

# Iniciar si no está corriendo
cd RabbitMQ/
docker-compose up -d

# Acceder a Management UI
http://localhost:15672 (guest/guest)
```

---

### Problema: JWT token inválido o expirado

**Error:** `401 Unauthorized`

**Solución:**
1. Obtener nuevo token desde Keycloak
2. Enviar en encabezado: `Authorization: Bearer {token}`
3. Verificar que el token no haya expirado

---

### Problema: Puerto ya está en uso

**Síntoma:** `Address already in use` en startup

**Solución (Opción 1):** Cambiar puerto en properties
```yaml
server:
  port: 9090
```

**Solución (Opción 2):** Matar proceso que usa puerto
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID {PID} /F

# Linux/macOS
lsof -i :8080
kill -9 {PID}
```

---

### Problema: Eureka no registra el microservicio

**Síntoma:** Microservicio no aparece en `http://localhost:8761`

**Verificaciones:**
1. ¿Eureka Server está corriendo? → `http://localhost:8761`
2. ¿EUREKA_URL está bien configurado?
3. Revisar logs para ver si hay error de conexión

```bash
# En los logs, buscar:
Registering service PAYMENTS with Eureka
```

---

### Problema: Migrations de base de datos fallan

**Síntoma:** Error `Table already exists` o `migration versions mismatch`

**Solución:**
1. Verificar valor de `DB_HIBERNATE_DDL_AUTO`:
   - `create-drop`: Borra BD en startup (desarrollo)
   - `update`: Actualiza esquema
   - `validate`: Solo valida

```bash
# Reset completo de BD
psql -U postgres -h localhost -d payments -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"

# Luego reiniciar microservicio
```

---

### Problema: Mensaje de error "Invoice not found"

**Causa:** La factura no existe en la réplica de facturas

**Solución:**
1. Verificar que la factura existe en `facture-management`
2. Esperar a que se sincronicen las réplicas
3. Revisar RabbitMQ para eventos de sincronización

```bash
# En logs, buscar:
Syncing invoice replica for invoice ID: {id}
```

---

### Problema: "Access Denied" en endpoint

**Síntoma:** `403 Forbidden` aunque token es válido

**Posibles causas:**
1. El usuario no tiene roles necesarios en Keycloak
2. Endpoint requiere permiso específico (verificar anotación `@PreAuthorize`)
3. Token no incluye los claims necesarios

**Solución:**
```bash
# Verificar token decodificado (jwt.io)
# Controlar que tenga:
{
  "preferred_username": "usuario",
  "resource_access": {
    "microservices_client": {
      "roles": ["admin", "user"]
    }
  }
}
```

---

## 📞 Contacto y Soporte

**Equipo de Desarrollo:** Equipo Dinámica - Escuadrón Lobo  
**Repositorio:** https://github.com/Equipo-dinamita-escuadron-lobo/ms-debt-payments  
**Issues:** [GitHub Issues](https://github.com/Equipo-dinamita-escuadron-lobo/ms-debt-payments/issues)

---

**Versión del documento:** 1.0
