using Microsoft.EntityFrameworkCore;
using valoraciones.Core.Domain;
using valoraciones.Core.Ports.Out;

namespace valoraciones.Infrastructure.Adapters.Out.Database
{
    public class RepositorioValoraciones : IRepositorioValoraciones
    {
        private readonly ValoracionesDbContext _context;

        public RepositorioValoraciones(ValoracionesDbContext context)
        {
            _context = context;
        }

        public async Task<Valoracion> AddAsync(Valoracion valoracion)
        {
            _context.Valoraciones.Add(valoracion);
            await _context.SaveChangesAsync();
            return valoracion;
        }

        public async Task<Valoracion?> GetByIdAsync(int id)
        {
            return await _context.Valoraciones.FindAsync(id);
        }

        public async Task<(IEnumerable<Valoracion> Items, int TotalCount)> GetByUsuarioAndRolAsync(string idUsuario, string rol, int page, int size)
        {
            var query = _context.Valoraciones
                .Where(v => v.IdValorado == idUsuario && v.RolValorado == rol);

            var totalCount = await query.CountAsync();
            var items = await query
                .OrderByDescending(v => v.FechaCreacion)
                .Skip((page - 1) * size)
                .Take(size)
                .ToListAsync();

            return (items, totalCount);
        }

        public async Task<Valoracion?> GetByCompraventaAndValoradorAsync(string idCompraventa, string idValorador)
        {
            return await _context.Valoraciones
                .FirstOrDefaultAsync(v => v.IdCompraventa == idCompraventa && v.IdValorador == idValorador);
        }
    }
}
