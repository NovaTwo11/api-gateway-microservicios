package co.edu.uniquindio.gateway.dto;

// DTO de Agregación. Contiene la información de ambos servicios.
public class UsuarioCompletoResponse {

    private UsuarioResponse usuario;
    private PerfilResponse perfil;

    // Constructor para la agregación
    public UsuarioCompletoResponse(UsuarioResponse usuario, PerfilResponse perfil) {
        this.usuario = usuario;
        this.perfil = perfil;
    }

    // Getters y Setters
    public UsuarioResponse getUsuario() { return usuario; }
    public void setUsuario(UsuarioResponse usuario) { this.usuario = usuario; }
    public PerfilResponse getPerfil() { return perfil; }
    public void setPerfil(PerfilResponse perfil) { this.perfil = perfil; }
}