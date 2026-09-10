namespace valoraciones.DTOs
{
    public class CompraventaDto
    {
        public string Id { get; set; } = string.Empty;
        public string IdProducto { get; set; } = string.Empty;
        public string IdComprador { get; set; } = string.Empty;
        public string IdVendedor { get; set; } = string.Empty;
        public double Precio { get; set; }
        public DateTime FechaCreacion { get; set; }
        public string Estado { get; set; } = string.Empty;
    }
}
