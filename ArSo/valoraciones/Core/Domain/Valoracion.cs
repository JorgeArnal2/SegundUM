namespace valoraciones.Core.Domain
{
    public class Valoracion
    {
        public int Id { get; set; }
        public string IdCompraventa { get; set; } = string.Empty;
        public string IdValorador { get; set; } = string.Empty;
        public string IdValorado { get; set; } = string.Empty;
        public string RolValorado { get; set; } = string.Empty; // "COMPRADOR" o "VENDEDOR"
        public int Puntuacion { get; set; } // 1 a 5
        public string? Comentario { get; set; }
        public DateTime FechaCreacion { get; set; } = DateTime.UtcNow;
    }
}
