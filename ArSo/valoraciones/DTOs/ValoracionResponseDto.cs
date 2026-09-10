namespace valoraciones.DTOs
{
    public class ValoracionResponseDto
    {
        public int Id { get; set; }
        public string IdCompraventa { get; set; } = string.Empty;
        public string IdValorador { get; set; } = string.Empty;
        public string IdValorado { get; set; } = string.Empty;
        public string RolValorado { get; set; } = string.Empty;
        public int Puntuacion { get; set; }
        public string? Comentario { get; set; }
        public DateTime FechaCreacion { get; set; }
        public List<LinkDto> Links { get; set; } = new List<LinkDto>();
    }
}
