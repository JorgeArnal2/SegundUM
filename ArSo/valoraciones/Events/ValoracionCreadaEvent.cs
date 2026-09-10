namespace valoraciones.Events
{
    public class ValoracionCreadaEvent
    {
        public string IdentificadorEntidad { get; set; } = string.Empty;
        public string TipoEvento { get; set; } = "valoracion-creada";
        public DateTime FechaHora { get; set; } = DateTime.UtcNow;
        public string IdUsuarioValorado { get; set; } = string.Empty;
        public string RolUsuarioValorado { get; set; } = string.Empty; // "COMPRADOR" o "VENDEDOR"
        public int Puntuacion { get; set; }
    }
}
