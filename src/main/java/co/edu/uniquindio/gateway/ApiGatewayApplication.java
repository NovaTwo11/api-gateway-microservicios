package co.edu.uniquindio.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    /**
     * Provee un builder de WebClient para que los controladores
     * y servicios puedan inyectarlo y hacer llamadas HTTP.
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    // Builder para el NUEVO servicio de Perfiles (Go)
    @Bean
    public WebClient.Builder profileServiceWebClientBuilder() {
        return WebClient.builder()
                .baseUrl("http://profile-service-go:8081");
    }

    // Builder para el servicio de Usuarios (Java)
    @Bean
    public WebClient.Builder tallerApiWebClientBuilder() {
        return WebClient.builder()
                .baseUrl("http://taller-api-2:8080");
    }
}