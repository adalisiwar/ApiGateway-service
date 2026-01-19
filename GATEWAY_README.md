# Food Delivery API Gateway

A Spring Cloud Gateway implementation for the Food Delivery microservices architecture. This gateway provides centralized request routing, JWT authentication, CORS handling, and load balancing.

## Features

✅ **Service Routing** - Routes requests to different microservices based on URL patterns  
✅ **JWT Authentication** - Validates JWT tokens for protected endpoints  
✅ **CORS Support** - Handles cross-origin requests with proper CORS headers  
✅ **Load Balancing** - Integrates with Eureka for service discovery and load balancing  
✅ **Security** - Spring Security configuration for request filtering and authorization  
✅ **Health Checks** - Built-in health and readiness check endpoints  
✅ **Logging** - Comprehensive logging for debugging and monitoring  

## Architecture Overview

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       ▼
┌─────────────────────────────────────┐
│      API Gateway (Port 8080)        │
├─────────────────────────────────────┤
│  ✓ JWT Filter                       │
│  ✓ CORS Configuration               │
│  ✓ Route Locator                    │
│  ✓ Security Configuration           │
└──────┬──────────────────────────────┘
       │
       ├──────────────────┬──────────────────┬──────────────────┬──────────────────┐
       │                  │                  │                  │                  │
       ▼                  ▼                  ▼                  ▼                  ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Auth        │    │ User        │    │ Restaurant  │    │ Order       │    │ Payment     │
│ Service     │    │ Service     │    │ Service     │    │ Service     │    │ Service     │
│ (8081)      │    │ (8082)      │    │ (8083)      │    │ (8084)      │    │ (8085)      │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

## Microservices Routes

### Auth Service (Port 8081)
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/refresh` - Refresh token

### User Service (Port 8082)
- `POST /api/users/register` - Register new user (Public)
- `POST /api/users/login` - Login (Public)
- `GET /api/users/{id}` - Get user profile (Protected)
- `PUT /api/users/{id}` - Update user profile (Protected)
- `DELETE /api/users/{id}` - Delete user account (Protected)
- `GET /api/users/profile/address` - Get user addresses (Protected)

### Restaurant Service (Port 8083)
- `GET /api/restaurants` - List all restaurants (Public)
- `GET /api/restaurants/{id}` - Get restaurant details (Public)
- `GET /api/menu/{restaurantId}` - Get restaurant menu (Public)
- `POST /api/restaurants` - Create restaurant (Protected)
- `PUT /api/restaurants/{id}` - Update restaurant (Protected)
- `DELETE /api/restaurants/{id}` - Delete restaurant (Protected)
- `POST /api/menu` - Add menu items (Protected)
- `PUT /api/menu/{id}` - Update menu items (Protected)

### Order Service (Port 8084)
- `POST /api/orders` - Create order (Protected)
- `GET /api/orders/{id}` - Get order details (Protected)
- `GET /api/order-history` - Get user's order history (Protected)
- `PUT /api/orders/{id}` - Update order (Protected)
- `DELETE /api/orders/{id}` - Cancel order (Protected)

### Payment Service (Port 8085)
- `POST /api/payments` - Process payment (Protected)
- `GET /api/payments/{id}` - Get payment details (Protected)
- `GET /api/payments/history` - Get payment history (Protected)

### Delivery Service (Port 8086)
- `GET /api/delivery/{orderId}` - Get delivery status (Protected)
- `GET /api/tracking/{orderId}` - Track order (Protected)
- `PUT /api/delivery/{id}` - Update delivery status (Protected)

### Review Service (Port 8087)
- `GET /api/reviews` - Get reviews (Public)
- `GET /api/reviews/{id}` - Get review details (Public)
- `POST /api/reviews` - Create review (Protected)
- `PUT /api/reviews/{id}` - Update review (Protected)
- `DELETE /api/reviews/{id}` - Delete review (Protected)

### Notification Service (Port 8088)
- `GET /api/notifications` - Get notifications (Protected)
- `POST /api/notifications` - Create notification (Protected)
- `PUT /api/notifications/{id}` - Update notification (Protected)

## Configuration

### JWT Configuration
Located in `application.properties`:
```properties
jwt.secret=your-secret-key-for-jwt-token-generation-should-be-at-least-32-characters-long
jwt.expiration=3600000 # 1 hour in milliseconds
```

### CORS Configuration
The gateway allows requests from:
- `http://localhost:3000` (Frontend)
- `http://localhost:3001` (Admin panel)
- `http://localhost:8080` (Internal)
- `http://localhost:8081` (Services)
- `http://localhost:8082` (Services)

Allowed methods: `GET`, `POST`, `PUT`, `DELETE`, `PATCH`, `OPTIONS`

### Eureka Service Discovery
```properties
eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka
```

## Starting the Gateway

### Prerequisites
- Java 17+
- Maven 3.6+
- Spring Boot 4.0.1
- Spring Cloud 2025.1.0

### Build
```bash
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

Or using JAR:
```bash
java -jar target/api-gateway-0.0.1-SNAPSHOT.jar
```

The gateway will start on `http://localhost:8080`

## API Usage Examples

### Health Check
```bash
curl -X GET http://localhost:8080/api/gateway/health
```

### Gateway Info
```bash
curl -X GET http://localhost:8080/api/gateway/info
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "user@example.com", "password": "password"}'
```

### Using JWT Token
```bash
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer <JWT_TOKEN_HERE>" \
  -H "Content-Type: application/json"
```

### Get Restaurants (Public)
```bash
curl -X GET http://localhost:8080/api/restaurants
```

### Create Order (Protected)
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer <JWT_TOKEN_HERE>" \
  -H "Content-Type: application/json" \
  -d '{"restaurantId": "123", "items": [...], "deliveryAddress": "..."}'
```

## Request/Response Headers

### Request Headers
- `Authorization: Bearer <token>` - JWT token for authentication
- `Content-Type: application/json` - Specifies JSON content

### Response Headers (Added by Gateway)
- `X-User-Id` - User ID from token
- `X-User-Email` - User email from token
- `X-User-Role` - User role from token

These headers are automatically added to all authenticated requests and available to downstream microservices.

## Filter Chain

1. **CORS Filter** - Handles cross-origin requests
2. **JWT Auth Filter** - Validates JWT tokens for protected routes
3. **Route Filter** - Routes requests to appropriate microservices
4. **Security Filter** - Applies Spring Security rules

## Logging

Enable detailed logging by updating `application.properties`:
```properties
logging.level.com.example.api_gateway=DEBUG
logging.level.org.springframework.cloud.gateway=DEBUG
logging.level.org.springframework.security=DEBUG
```

## Error Handling

### 401 Unauthorized
- Missing Authorization header
- Invalid or expired token
- Token validation failed

### 403 Forbidden
- User role doesn't have access

### 404 Not Found
- Route doesn't match any service

### 500 Internal Server Error
- Downstream service unavailable

## Production Deployment

### Change JWT Secret
**IMPORTANT:** Change the JWT secret in production:
```properties
jwt.secret=<your-secure-randomly-generated-key-minimum-32-chars>
```

### Enable HTTPS
```properties
server.ssl.key-store-type=PKCS12
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=<password>
server.ssl.key-alias=tomcat
```

### Configure Production CORS Origins
Update allowed origins for your frontend URLs:
```properties
spring.cloud.gateway.globalcors.corsConfigurations.[/**].allowedOrigins=https://yourdomain.com
```

### Enable Rate Limiting
Add dependency:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway-rate-limiting</artifactId>
</dependency>
```

## Monitoring

### Actuator Endpoints
- `/actuator/health` - Health status
- `/actuator/metrics` - Application metrics
- `/actuator/prometheus` - Prometheus metrics

### Gateway Endpoints
- `GET /api/gateway/health` - Gateway health check
- `GET /api/gateway/info` - Gateway information
- `GET /api/gateway/ready` - Gateway readiness probe

## Troubleshooting

### Gateway not routing requests
1. Check if microservices are running
2. Verify Eureka server is running (if using service discovery)
3. Check routes in `GatewayConfig.java`

### JWT token validation fails
1. Ensure JWT secret matches between services
2. Check token expiration time
3. Verify Authorization header format: `Bearer <token>`

### CORS errors
1. Check if frontend URL is in allowed origins
2. Verify allowed methods include your request method
3. Check exposed headers configuration

### Service unavailable (503)
1. Restart the microservice
2. Check service registration in Eureka
3. Verify service ports are correct

## File Structure

```
src/main/java/com/example/api_gateway/
├── ApiGatewayApplication.java       # Main application class
├── config/
│   ├── GatewayConfig.java          # Route configuration
│   └── SecurityConfig.java         # Security configuration
├── controller/
│   └── GatewayController.java      # Gateway endpoints
├── filter/
│   └── JwtAuthFilter.java          # JWT authentication filter
├── dto/
│   └── ApiResponse.java            # Common response DTO
└── util/
    └── JwtTokenProvider.java       # JWT token utilities
```

## Contributing

1. Create a feature branch
2. Make your changes
3. Test thoroughly
4. Submit a pull request

## License

This project is part of the Food Delivery microservices application.

## Support

For issues or questions, contact the development team.
