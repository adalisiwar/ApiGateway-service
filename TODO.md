# TODO: Convert to Reactive Spring Security

## 1. Convert JwtAuthenticationFilter to WebFilter ✅
- Change from extending OncePerRequestFilter to implementing WebFilter
- Use ServerWebExchange instead of HttpServletRequest/Response
- Use ReactiveSecurityContextHolder for setting authentication
- Extract JWT from Authorization header reactively
- Validate token and set authentication in reactive context

## 2. Convert SecurityConfig to Reactive ✅
- Change @EnableWebSecurity to @EnableWebFluxSecurity
- Change SecurityFilterChain to SecurityWebFilterChain
- Configure CORS using reactive CorsConfigurationSource
- Add WebFilter to the security chain
- Update authorization rules for reactive context

## 3. Update GatewayConfig ✅
- Remove JwtAuthenticationFilter from all route filters
- Rely on global reactive security for authentication
- Ensure routes are properly configured without per-route auth filters

## 4. Test Changes ✅
- Verify JWT authentication works in reactive context
- Check that gateway endpoints are secured
- Ensure routing still works correctly
