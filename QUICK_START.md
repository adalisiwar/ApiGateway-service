# Quick Start Guide - API Gateway

## Step 1: Prerequisites

Make sure you have the following installed:
- **Java 17 or higher** - [Download](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- **Maven 3.6+** - [Download](https://maven.apache.org/download.cgi)
- **Git** - [Download](https://git-scm.com/)

Verify installation:
```bash
java -version
mvn -version
```

## Step 2: Project Setup

### Option A: Clone and Setup
```bash
# Navigate to your workspace
cd c:\Users\adali\OneDrive\Desktop\api-gateway

# Install dependencies
mvn clean install
```

### Option B: Fresh Build
```bash
# Clean and build
mvn clean package -DskipTests

# Output will be in: target/api-gateway-0.0.1-SNAPSHOT.jar
```

## Step 3: Configuration

### Edit JWT Secret (IMPORTANT!)
Open `src/main/resources/application.properties` and update:
```properties
jwt.secret=your-custom-secret-key-minimum-32-characters-should-be-random
```

### For Local Development
Keep the default configuration - it's pre-configured for local microservices running on ports 8081-8088.

## Step 4: Start the Gateway

### Option A: Using Maven
```bash
mvn spring-boot:run
```

### Option B: Using JAR
```bash
java -jar target/api-gateway-0.0.1-SNAPSHOT.jar
```

### Option C: Using IDE (IntelliJ/Eclipse)
1. Open the project
2. Find `ApiGatewayApplication.java`
3. Right-click → Run

## Step 5: Verify Gateway is Running

```bash
# Check health
curl http://localhost:8080/api/gateway/health

# Expected response:
# {
#   "status": 200,
#   "message": "Gateway is healthy",
#   "data": {
#     "status": "UP",
#     "service": "Food Delivery API Gateway",
#     "version": "1.0.0",
#     "timestamp": "..."
#   }
# }
```

## Step 6: Test Public Endpoints

### Get Restaurants (No Auth Needed)
```bash
curl http://localhost:8080/api/restaurants
```

### Get Restaurant Menu (No Auth Needed)
```bash
curl http://localhost:8080/api/menu/restaurant123
```

## Step 7: Test Protected Endpoints

### 1. Login (Get Token)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user@example.com",
    "password": "password123"
  }'

# Expected response includes:
# {
#   "token": "eyJhbGciOiJIUzI1NiJ9...",
#   "userId": "user123",
#   "role": "CUSTOMER"
# }
```

### 2. Use Token for Protected Endpoint
```bash
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

## Starting Microservices (for Testing)

The gateway expects these microservices to be running:

### Minimal Setup (for testing gateway only)
If you don't have all microservices running yet, you can still test the gateway:

1. **Start Auth Service** (8081)
2. **Start User Service** (8082)
3. **Start Restaurant Service** (8083)

Then test these routes:
```bash
# Public route
curl http://localhost:8080/api/restaurants

# Protected route (requires token)
curl -H "Authorization: Bearer <token>" http://localhost:8080/api/users/profile
```

## Common Issues & Solutions

### Issue: "Connection refused" error
**Solution:** Make sure the downstream microservice is running on the correct port.

### Issue: "No route matches" error
**Solution:** Check the path matches a defined route in `GatewayConfig.java`.

### Issue: "Unauthorized" (401) error
**Solution:** 
1. Make sure you include the Authorization header
2. Format: `Authorization: Bearer <token>`
3. Check if token is expired

### Issue: CORS error in browser
**Solution:** The gateway already handles CORS, but verify:
1. Frontend is on allowed origins (localhost:3000, localhost:3001, etc.)
2. Request includes proper Content-Type header

## Development Workflow

### 1. Make Code Changes
Edit files in `src/main/java/com/example/api_gateway/`

### 2. Rebuild (if running with IDE)
- Most IDEs auto-compile
- Or manually: `mvn clean install`

### 3. Restart Gateway
- Stop the running instance (Ctrl+C)
- Run `mvn spring-boot:run` again

### 4. Test Changes
Use curl or Postman to test your changes

## Project Structure

```
api-gateway/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/api_gateway/
│   │   │       ├── ApiGatewayApplication.java    (Main class)
│   │   │       ├── config/
│   │   │       │   ├── GatewayConfig.java       (Routes)
│   │   │       │   └── SecurityConfig.java      (Security)
│   │   │       ├── controller/
│   │   │       │   └── GatewayController.java   (Endpoints)
│   │   │       ├── filter/
│   │   │       │   └── JwtAuthFilter.java       (Auth)
│   │   │       ├── dto/
│   │   │       │   └── ApiResponse.java         (Response)
│   │   │       └── util/
│   │   │           └── JwtTokenProvider.java    (JWT)
│   │   └── resources/
│   │       └── application.properties            (Config)
│   └── test/
│       └── java/com/example/api_gateway/
│           └── ApiGatewayApplicationTests.java
├── pom.xml                                       (Maven config)
├── GATEWAY_README.md                             (Full documentation)
├── QUICK_START.md                                (This file)
└── application-example.yml                       (Profile examples)
```

## Next Steps

1. **Read Full Documentation** - Check `GATEWAY_README.md` for detailed info
2. **Review Code** - Understand the routing in `GatewayConfig.java`
3. **Implement Your Routes** - Add routes to your microservices
4. **Add Custom Filters** - Create application-specific filters
5. **Set up Monitoring** - Configure logging and metrics
6. **Deploy to Production** - Follow production guidelines in docs

## Useful Commands

```bash
# Build project
mvn clean install

# Run tests
mvn test

# Build without tests
mvn clean package -DskipTests

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# View logs
mvn spring-boot:run 2>&1 | grep "ERROR\|WARN\|INFO"

# Check if port is in use
netstat -tulnap | grep 8080 (Linux/Mac)
netstat -ano | findstr :8080 (Windows)

# Kill process on port 8080
lsof -ti:8080 | xargs kill -9 (Linux/Mac)
```

## Testing with Postman

1. Import the collection from workspace
2. Set variables:
   - `base_url`: http://localhost:8080
   - `token`: (obtained after login)
3. Run requests in order

## Production Deployment

When ready to deploy:

1. **Change JWT Secret** in environment variables
2. **Enable HTTPS** in `application.properties`
3. **Update CORS Origins** for production URLs
4. **Configure Eureka** for service discovery
5. **Enable Monitoring** and logging
6. **Set up Rate Limiting** to prevent abuse

See `GATEWAY_README.md` for production checklist.

## Support & Troubleshooting

For detailed troubleshooting, see the "Troubleshooting" section in `GATEWAY_README.md`.

**Happy coding! 🚀**
