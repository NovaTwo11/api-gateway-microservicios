package co.edu.uniquindio.gateway.dto;

import java.util.UUID;

// DTO espejo de co.edu.uniquindio.tallerapi2.dto.UsuarioResponse
// Necesario para que el Gateway pueda deserializar la respuesta del microservicio
public class UsuarioResponse {
    private UUID id;
    private String nombre;
    private String email;

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}