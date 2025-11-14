package co.edu.uniquindio.gateway.dto;

import java.util.Map;
import java.util.UUID;

// DTO espejo de co.edu.uniquindio.tallerapi2.dto.PerfilResponse
public class PerfilResponse {
    private UUID userId;
    private String apodo;
    private String biografia;
    private String paginaPersonal;
    private String direccion;
    private String organizacion;
    private String paisResidencia;
    private Boolean informacionPublica;
    private Map<String, String> redesSociales;

    // Getters y Setters
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getApodo() { return apodo; }
    public void setApodo(String apodo) { this.apodo = apodo; }
    public String getBiografia() { return biografia; }
    public void setBiografia(String biografia) { this.biografia = biografia; }
    public String getPaginaPersonal() { return paginaPersonal; }
    public void setPaginaPersonal(String paginaPersonal) { this.paginaPersonal = paginaPersonal; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getOrganizacion() { return organizacion; }
    public void setOrganizacion(String organizacion) { this.organizacion = organizacion; }
    public String getPaisResidencia() { return paisResidencia; }
    public void setPaisResidencia(String paisResidencia) { this.paisResidencia = paisResidencia; }
    public Boolean getInformacionPublica() { return informacionPublica; }
    public void setInformacionPublica(Boolean informacionPublica) { this.informacionPublica = informacionPublica; }
    public Map<String, String> getRedesSociales() { return redesSociales; }
    public void setRedesSociales(Map<String, String> redesSociales) { this.redesSociales = redesSociales; }
}