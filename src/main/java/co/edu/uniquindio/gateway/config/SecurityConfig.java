package co.edu.uniquindio.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.server.SecurityWebFilterChain;

// --- Imports Añadidos ---
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
// --- Fin Imports Añadidos ---

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ReactiveJwtDecoder reactiveJwtDecoder) {
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/realms/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtDecoder(reactiveJwtDecoder))
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable);
        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        NimbusReactiveJwtDecoder jwtDecoder = new NimbusReactiveJwtDecoder(this.jwkSetUri);
        OAuth2TokenValidator<Jwt> defaultValidator = JwtValidators.createDefault();
        jwtDecoder.setJwtValidator(defaultValidator);
        return jwtDecoder;
    }

    /**
     * Filtro global que se ejecuta DESPUÉS de la autenticación.
     * Si la ruta es para '/api/perfiles', extrae el ID de usuario (claim 'sub')
     * del token y lo inyecta en el header 'X-User-ID' para el servicio de Go.
     */
    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE) // Se ejecuta después de los filtros de seguridad
    public GlobalFilter addUserIdHeaderFilter() {
        return (exchange, chain) -> {

            // 1. Comprobar si la ruta es la de perfiles
            if (exchange.getRequest().getPath().value().startsWith("/api/perfiles")) {

                // 2. Obtener el Principal (usuario autenticado) del contexto
                return exchange.getPrincipal()
                        .flatMap(principal -> {
                            String userId = null;

                            // 3. Extraer el ID de usuario (claim 'sub') del token JWT
                            if (principal instanceof JwtAuthenticationToken) {
                                Jwt jwt = ((JwtAuthenticationToken) principal).getToken();
                                userId = jwt.getClaimAsString("sub");
                            }

                            // 4. Si encontramos el ID, mutar la petición
                            if (userId != null) {
                                ServerHttpRequest request = exchange.getRequest().mutate()
                                        .header("X-User-ID", userId)
                                        .build();
                                ServerWebExchange newExchange = exchange.mutate().request(request).build();
                                return chain.filter(newExchange);
                            }

                            // Si no hay ID (raro), continuar sin modificar
                            return chain.filter(exchange);
                        });
            }

            // Si no es la ruta de perfiles, no hacer nada.
            return chain.filter(exchange);
        };
    }
}