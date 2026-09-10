package com.arso.compraventas.port.dto;

/**
 * DTO con los campos del microservicio Productos que necesita Compraventas.
 */
public class ProductoRemotoDto {

    private String id;
    private String titulo;
    private Double precio;
    private boolean vendido;
    private String idVendedor;
    private RecogidaDto recogida;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public boolean isVendido() { return vendido; }
    public void setVendido(boolean vendido) { this.vendido = vendido; }

    // El vendedor viene anidado en el DTO de Productos
    private VendedorDto vendedor;

    public VendedorDto getVendedor() { return vendedor; }
    public void setVendedor(VendedorDto vendedor) { this.vendedor = vendedor; }

    public String getIdVendedor() {
        return vendedor != null ? vendedor.getId() : idVendedor;
    }

    public RecogidaDto getRecogida() { return recogida; }
    public void setRecogida(RecogidaDto recogida) { this.recogida = recogida; }

    /** Sub-DTO para el vendedor del producto */
    public static class VendedorDto {
        private String id;
        private String nombre;
        private String apellidos;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getApellidos() { return apellidos; }
        public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    }

    /** Sub-DTO para la recogida del producto */
    public static class RecogidaDto {
        private String descripcion;
        private Double longitud;
        private Double latitud;

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        public Double getLongitud() { return longitud; }
        public void setLongitud(Double longitud) { this.longitud = longitud; }
        public Double getLatitud() { return latitud; }
        public void setLatitud(Double latitud) { this.latitud = latitud; }

        public String toTexto() {
            if (descripcion != null && !descripcion.isBlank()) return descripcion;
            if (longitud != null && latitud != null) return latitud + "," + longitud;
            return "";
        }
    }
}
