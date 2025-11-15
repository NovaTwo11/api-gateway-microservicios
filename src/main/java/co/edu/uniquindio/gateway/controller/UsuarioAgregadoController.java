package co.edu.uniquindio.gateway.controller;

import co.edu.uniquindio.gateway.dto.PerfilResponse;
import co.edu.uniquindio.gateway.dto.UsuarioCompletoResponse;
import co.edu.uniquindio.gateway.dto.UsuarioResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.UUID;

@RestController
@RequestMapping("/api/usuario-completo")
public class UsuarioAgregadoController {

    private final WebClient webClientTallerApi;
    private final WebClient webClientProfileService;

    public UsuarioAgregadoController(
            @Qualifier("tallerApiWebClientBuilder") WebClient.Builder tallerApiBuilder,
            @Qualifier("profileServiceWebClientBuilder") WebClient.Builder profileBuilder) {

        this.webClientTallerApi = tallerApiBuilder.build();
        this.webClientProfileService = profileBuilder.build();
    }

    /**
     * Implementa el requisito R6d:
     * 1. Recibe una solicitud de "datos completos".
     * 2. Divide la solicitud en dos llamadas (Fan-out).
     * 3. Unifica las respuestas (Fan-in).
     * 4. Devuelve una respuesta unificada.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<UsuarioCompletoResponse>> getUsuarioCompleto(
            @PathVariable String id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

        // 1. (Fan-out) Preparamos la llamada al endpoint de Usuarios
        Mono<UsuarioResponse> usuarioMono = webClientTallerApi.get()
                .uri("/api/usuarios/" + id)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader) // Pasar el token
                .retrieve()
                // Si el usuario no existe, lanzamos un 404
                .onStatus(
                        status -> status == HttpStatus.NOT_FOUND,
                        clientResponse -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"))
                )
                .bodyToMono(UsuarioResponse.class);

        // 2. (Fan-out) Preparamos la llamada al endpoint de Perfiles
        Mono<PerfilResponse> perfilMono = webClientProfileService.get()
                .uri("/api/perfiles/por-usuario/" + id)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader) // Pasar el token
                .retrieve()
                .bodyToMono(PerfilResponse.class)
                // Si el perfil no existe (404) o da error, simplemente devolvemos un objeto vacío
                .onErrorResume(e -> Mono.just(new PerfilResponse()));

        // 3. (Fan-in) Combinamos ambas respuestas cuando estén listas
        return Mono.zip(usuarioMono, perfilMono)
                .map(tupla -> {
                    // tupla.getT1() es la respuesta de usuarioMono
                    // tupla.getT2() es la respuesta de perfilMono
                    return new UsuarioCompletoResponse(tupla.getT1(), tupla.getT2());
                })
                .map(respuestaUnificada -> ResponseEntity.ok(respuestaUnificada));
    }
}