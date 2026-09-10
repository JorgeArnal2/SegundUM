using valoraciones.Core.Domain;

namespace valoraciones.Core.Ports.In
{
    public interface IServicioValoraciones
    {
        Task<Valoracion> RegistrarValoracionVendedorAsync(string idCompraventa, string idComprador, int puntuacion, string? comentario);
        Task<Valoracion> RegistrarValoracionCompradorAsync(string idCompraventa, string idVendedor, int puntuacion, string? comentario);
        Task<(IEnumerable<Valoracion> Items, int TotalCount)> ConsultarValoracionesComoVendedorAsync(string idUsuario, int page, int size);
        Task<(IEnumerable<Valoracion> Items, int TotalCount)> ConsultarValoracionesComoCompradorAsync(string idUsuario, int page, int size);
        Task<Valoracion?> RecuperarValoracionAsync(int id);
    }
}
