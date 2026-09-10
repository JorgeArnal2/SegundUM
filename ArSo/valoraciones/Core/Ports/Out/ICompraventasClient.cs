using valoraciones.DTOs;

namespace valoraciones.Core.Ports.Out
{
    public interface ICompraventasClient
    {
        Task<CompraventaDto?> ObtenerCompraventaAsync(string idCompraventa);
    }
}
