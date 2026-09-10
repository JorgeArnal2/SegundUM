namespace valoraciones.DTOs
{
    public class CreateValoracionDto
    {
        public string IdCompraventa { get; set; } = string.Empty;
        public string IdUsuarioRegistra { get; set; } = string.Empty;
        public int Puntuacion { get; set; }
        public string? Comentario { get; set; }
    }
}
