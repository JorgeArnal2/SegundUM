using valoraciones.Core.Domain;

namespace valoraciones.Core.Ports.Out
{
    public interface IRepositorioValoraciones
    {
        Task<Valoracion> AddAsync(Valoracion valoracion);
        Task<Valoracion?> GetByIdAsync(int id);
        Task<(IEnumerable<Valoracion> Items, int TotalCount)> GetByUsuarioAndRolAsync(string idUsuario, string rol, int page, int size);
        Task<Valoracion?> GetByCompraventaAndValoradorAsync(string idCompraventa, string idValorador);
    }
}
