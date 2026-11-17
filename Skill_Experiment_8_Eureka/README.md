# Skill Experiment 8 — Service Discovery with Eureka

A simple Spring Boot microservices lab with:
- Eureka Server (`8761`) for service registration and discovery
- Two or more Inventory Service instances (`8081`, `8082`, ...)
- API Gateway (`8080`) with client-side load balancing

## Requirements
- Java 17 or later
- Maven 3.8+
- Internet access on the first build so Maven can download dependencies

## 1. Build the project
Open a terminal in this folder and run:

```bash
mvn clean package -DskipTests
```

## 2. Start Eureka Server
Open Terminal 1:

```bash
mvn -pl eureka-server spring-boot:run
```

Open the dashboard: http://localhost:8761

## 3. Start multiple Inventory Service instances
Open Terminal 2:

```bash
mvn -pl inventory-service spring-boot:run
```

Open Terminal 3 (same service, different port):

```bash
mvn -pl inventory-service spring-boot:run -Dspring-boot.run.arguments=--server.port=8082
```

Optional third instance (Terminal 4):

```bash
mvn -pl inventory-service spring-boot:run -Dspring-boot.run.arguments=--server.port=8083
```

Wait around 20–30 seconds and refresh the Eureka dashboard. You should see multiple `INVENTORY-SERVICE` instances.

## 4. Start API Gateway
Open another terminal:

```bash
mvn -pl api-gateway spring-boot:run
```

## 5. Test load balancing
Send several requests to the Gateway (not directly to port 8081):

```bash
curl http://localhost:8080/api/inventory
curl http://localhost:8080/api/inventory/101
```

Repeat the first command several times. The JSON response includes `port`; it should show requests being served by different registered instances over repeated calls. The exact sequence is not guaranteed.

In Windows PowerShell, repeat requests:

```powershell
1..10 | ForEach-Object { Invoke-RestMethod http://localhost:8080/api/inventory | Select-Object port, instance }
```

## 6. Test failure handling
1. Stop one Inventory Service terminal with `Ctrl+C`.
2. Wait for Eureka to detect the stopped instance (usually up to the lease/heartbeat interval; allow roughly 30–90 seconds).
3. Send requests again through `http://localhost:8080/api/inventory`.
4. The Gateway should continue routing to the remaining registered instance(s). If the stopped instance briefly appears in Eureka, wait and retry.

## Direct service tests
- http://localhost:8081/api/inventory
- http://localhost:8082/api/inventory
- Eureka dashboard: http://localhost:8761
- Gateway endpoint: http://localhost:8080/api/inventory

## Suggested Git commands

```bash
git init
git add .
git commit -m "Add Eureka service discovery lab"
```

## Architecture

```text
             +---------------------+
             |    Eureka Server    |
             |     localhost:8761  |
             +----------+----------+
                        ^
              registers | discovers
              +---------+----------+
              |                    |
     +--------+--------+   +-------+---------+
     | Inventory :8081 |   | Inventory :8082 |
     +-----------------+   +-----------------+
              ^                    ^
              +----------+---------+
                         |
                 +-------+-------+
                 |   API Gateway |
                 | localhost:8080|
                 +-------+-------+
                         ^
                    Client requests
```

## Expected observations
- Eureka dashboard lists the gateway and each Inventory Service instance.
- Gateway requests are load-balanced across available Inventory Service instances.
- After an instance is stopped and Eureka updates its registry, requests continue through the remaining instance(s).

**Note:** This is a lab/demo configuration. Eureka registration and health status are eventually consistent, so discovery changes are not instantaneous.
