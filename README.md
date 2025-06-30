# Configurable Form and Workflow Engine

## 📌 Overview

This project is a modular and extensible system that allows:

- 🛠️ **Admins** to configure dynamic form templates via JSON Schema.
- 🔄 **Admins** to configure state-machine-based workflows.
- 📝 **Users** to submit forms that trigger associated workflows.
- 🔐 **Role-based users** to progress the workflow through different states.
- 🧩 **Authentication and Role Management** handled securely via Keycloak.

---

## 🧱 Tech Stack

| Layer        | Technology     |
|--------------|----------------|
| Frontend     | Next.js        |
| Backend      | Spring Boot    |
| Auth         | Keycloak       |
| Database     | PostgreSQL     |
| Workflow     | Flowable BPMN  |

---

## 🧩 Features

### 🔧 Admin Features
- Configure dynamic **form templates** using JSON Schema.
- Define **workflows** with:
    - States (e.g., `Draft`, `Review`, `Approved`, `Closed`)
    - Transitions with allowed roles (e.g., `Employee`, `Manager`, `HR`)

### 🧑‍💼 User Features
- Authenticate via **Keycloak**.
- Fill and submit forms.
- Trigger associated workflows automatically.
- Transition workflow states if permitted by role.

---

## ⚙️ Architecture

1. **Frontend (Next.js)**
    - Fetches form templates and renders dynamic form UIs.
    - Calls backend APIs for submission and workflow transitions.
    - Integrates with Keycloak for login and role handling.

2. **Backend (Spring Boot + Flowable)**
    - Stores form templates and workflows in PostgreSQL.
    - On form submission:
        - Persists form data.
        - Starts a Flowable process for the associated workflow.
    - Enforces role-based transitions using Keycloak JWT validation.

3. **Workflow Engine (Flowable)**
    - Manages process instances.
    - Maps transitions based on role permissions.

4. **Authentication (Keycloak)**
    - Handles user login and role management.
    - Issues JWTs for secure API access.

---

## 📂 Folder Structure (Simplified)

```
frontend/
├── src/
│   ├── app/
│   │   ├── api/
|   │   │   ├── apply/                # API for Submission
|   │   │   ├── auth/                 # API for authentication
|   │   │   └── form-templates/        # API for Submission
│   │   ├── Head/
│   │   │   └── page.tsx          # Main Head dashboard
│   │   ├── Employee/
│   │   │   └── page.tsx          # Main Employee dashboard
|   │   │   ├── HR/
│   │   │   └── page.tsx          # Main HR dashboard
│   │   ├── Manager/
│   │   │   └── page.tsx          # Main Manager dashboard
│   │   ├── User/
│   │   │   └── page.tsx          # Main User dashboard
│   │   └── util/
│   │       └── auth.ts/   # Signout function
│   ├── components/
│   │   ├── FormTemplateCard.tsx  # Template display component
│   │   └── FormTemplateModal.tsx # Create/edit modal
│   ├── services/
│   │   └── formTemplateService.ts # API service layer
│   └── types/
|   |   ├── next-auth.d.tsx  # TypeScript interfaces
│       └── form-template.ts      # TypeScript interfaces
```

```
backend/
├── src/main/java/com.example.workflow/
│   ├── config/
|   │   └── Securityconfig        # Authorozation configuration 
│   ├── controller/
│   │   ├── FormSubmissionController               # API for Submission
│   │   ├── FormTemplateController                 # API for from template
│   │   ├── TaskController                         # API to get user task
│   │   └── WorkflowInstanceController             # API for workflow transition
│   ├── DTO/
│   │   └── TaskDto           # Get the response from the workflow
│   ├── Model/
│   │   ├── FormSubmission               
│   │   ├── FormTemplate                 
│   │   └── JsonToMapConverter           
│   ├── repositary/
|   │   ├── FormSubmissionRepositary               # API for Submission
│   │   └── FormTemplateRepositary                 # API for from template
|   └── service/
│   │   ├── FormSubmissionService              
│   │   └── FormTemplateService               
│   └── util/
│   |   └── validator      # Validate the form submitted
```


---

# 🚀 Getting Started

### Prerequisites
- Node.js, Maven, Docker
- PostgreSQL database
- Keycloak server

## 🌐 Frontend

### 1. Clone the Repository

```bash
git clone https://github.com/Priyansuvaish/frontend.git
cd frontend
```

### 2. Configure `.env`

```
KEYCLOAK_CLIENT_ID=<Your-Realm-Name>
KEYCLOAK_ISSUER=<Your-keyclock-url>/realms/<Your-Realm-Name>              #serverside variable
NEXT_PUBLIC_KEYCLOAK_ISSUER=<Your-keyclock-url>/realms/<Your-Realm-Name>  #clientside variable
NEXTAUTH_URL=<Your-Application-server>
BACKEND_URL=<Your-Springboot-server>                #serverside variable
NEXT_PUBLIC_BACKEND_URL=<Your-Springboot-server>    #clientside variable
NEXT_PUBLIC_TEMPLATE_ID=<Your-form-ID>              #serverside variable
TEMPLATE_ID=<Your-form-ID>                          #clientside variable
```

### 3. Install & Run

```bash
npm install
npm run build
npm run dev
```

## 🔧 Backend

### 1. Clone the Repository

```bash
git clone https://github.com/Priyansuvaish/backendworkflow.git
cd backendworkflow
```

### 2. Set Up Keyclock
- Download [keyclock](https://www.keycloak.org/)
- Configure realm, roles, and clients.
- Start Keycloak server.

### 3. Set Up Postgresql
- Download [PostgreSQL](https://www.postgresql.org/download/)
- Set the Enviroment variable
- Run the Postgresql
- Create a database.

### 4. Configure  `aplication.yml`

```
server:
  port: <Your-Port-Number>
spring:
  datasource:
    url: <Your-Postgresql-URL>/DatabaseName
    username: <Your-username>
    password: <Your-password>
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.PostgreSQLDialect
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: <Your-keyclock-url>/realms/<Your-Realm-Name>
keycloak:
  realm: <Your-Realm-Name>
  auth-server-url: <Your-keyclock-url>
  resource: <Your-Resource-Name>
  public-client: true
  bearer-only: true 
```

### 5. Build and Run
```bash
./mvnw clean install
java -jar target/backend.jar

```
# 🐳 Docker Deployment

### 1. Setup the docker
- Download the Docker
- Run the docker

### 2. Build Imagese
- Goto the frontend directory and run the following commmand:
```bash
docker build -t <name-of-frontend-image> .
```

- Goto the backendworkflow directory and run the following commmand:
```bash
docker build -t <name-of-frontend-image> .
```

### Run Containers
- To the frontend image, run this command:
```bash
docker run -p 3000:3000 -e NEXTAUTH_SECRET=your_secret -e NEXTAUTH_URL=<Your-Application-server> -e KEYCLOAK_ISSUER=<Your-keyclock-url>/realms/<Your-Realm-Name> -e NEXT_PUBLIC_KEYCLOAK_ISSUER=<Your-keyclock-url>/realms/<Your-Realm-Name> -e KEYCLOAK_CLIENT_ID=<Your-Realm-Name> -e BACKEND_URL=<Your-Springboot-server> -e NEXT_PUBLIC_BACKEND_URL=<Your-Spring boot-server> -e TEMPLATE_ID=<Your-form-ID> -e NEXT_PUBLIC_TEMPLATE_ID=<Your-form-ID> <name-of-frontend-image>
```

- To the backendworkflow image, run this command:
```bash
docker run -p <Your-Port-Number>:<Your-Port-Number> <name-of-frontend-image>
```
### 3. Or Use Docker Compose
- Goto the frontend directory and set the enviroment variable in the `docker-compose.yml`

🔁 Note: Replace localhost with your machine’s IP address in all URLs to allow access from within containers.
```
# Frontend Environment Variables
NEXTAUTH_SECRET: your_secret
KEYCLOAK_CLIENT_ID=<Your-Realm-Name>
KEYCLOAK_ISSUER=<Your-keyclock-url>/realms/<Your-Realm-Name>              #serverside variable
NEXT_PUBLIC_KEYCLOAK_ISSUER=<Your-keyclock-url>/realms/<Your-Realm-Name>  #clientside variable
NEXTAUTH_URL=<Your-Application-server>
BACKEND_URL=<Your-Springboot-server>                #serverside variable
NEXT_PUBLIC_BACKEND_URL=<Your-Springboot-server>    #clientside variable
NEXT_PUBLIC_TEMPLATE_ID=<Your-form-ID>              #serverside variable
TEMPLATE_ID=<Your-form-ID>                          #clientside variable

# Backend Environment Variables
SPRING_DATASOURCE_URL: jdbc:postgresql://host.docker.internal:5432/birth
      SPRING_DATASOURCE_USERNAME: <Your-username>
      SPRING_DATASOURCE_PASSWORD: <Your-paasword>
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
      SPRING_JPA_SHOW_SQL: "true"
      SPRING_JPA_DATABASE_PLATFORM: org.hibernate.dialect.PostgreSQLDialect
      SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI: <Your-keyclock-url>/realms/<Your-Realm-Name>
      KEYCLOAK_REALM: <Your-Realm-Name>
      KEYCLOAK_AUTH_SERVER_URL: <Your-keyclock-url>
      KEYCLOAK_RESOURCE: <Your-Resource-Name>
      KEYCLOAK_PUBLIC_CLIENT: "true"
      KEYCLOAK_BEARER_ONLY: "true"
```
- Run the docker-compose file as:
```bash
docker-compose up
```
