package com.arso.usuarios.event;

public class ValoracionCreadaEvent {
    private String identificadorEntidad;
    private String tipoEvento;
    private String fechaHora;
    private String idUsuarioValorado;
    private String rolUsuarioValorado; // "COMPRADOR" o "VENDEDOR"
    private int puntuacion;

    public String getIdentificadorEntidad() { return identificadorEntidad; }
    public void setIdentificadorEntidad(String identificadorEntidad) { this.identificadorEntidad = identificadorEntidad; }

    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }

    public String getFechaHora() { return fechaHora; }
    public void setFechaHora(String fechaHora) { this.fechaHora = fechaHora; }

    public String getIdUsuarioValorado() {
        return idUsuarioValorado;
    }

    public void setIdUsuarioValorado(String idUsuarioValorado) {
        this.idUsuarioValorado = idUsuarioValorado;
    }

    public String getRolUsuarioValorado() {
        return rolUsuarioValorado;
    }

    public void setRolUsuarioValorado(String rolUsuarioValorado) {
        this.rolUsuarioValorado = rolUsuarioValorado;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }
}
