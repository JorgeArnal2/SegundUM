using valoraciones.Core.Domain;
using valoraciones.Core.Ports.In;
using valoraciones.Core.Ports.Out;
using valoraciones.Events;

namespace valoraciones.Core.Services
{
    public class ServicioValoraciones : IServicioValoraciones
    {
        private readonly IRepositorioValoraciones _repositorio;
        private readonly ICompraventasClient _compraventasClient;
        private readonly IEventPublisher _eventPublisher;

        public ServicioValoraciones(
            IRepositorioValoraciones repositorio,
            ICompraventasClient compraventasClient,
            IEventPublisher eventPublisher)
        {
            _repositorio = repositorio;
            _compraventasClient = compraventasClient;
            _eventPublisher = eventPublisher;
        }

        public async Task<Valoracion> RegistrarValoracionVendedorAsync(string idCompraventa, string idComprador, int puntuacion, string? comentario)
        {
            if (puntuacion < 1 || puntuacion > 5)
            {
                throw new ArgumentException("La puntuación debe estar entre 1 y 5.");
            }

            var compraventa = await _compraventasClient.ObtenerCompraventaAsync(idCompraventa);
            if (compraventa == null)
            {
                throw new Exception("Compraventa no encontrada.");
            }

            if (compraventa.IdComprador != idComprador)
            {
                throw new UnauthorizedAccessException("El usuario no es el comprador de esta compraventa.");
            }

            var existing = await _repositorio.GetByCompraventaAndValoradorAsync(idCompraventa, idComprador);
            if (existing != null)
            {
                throw new InvalidOperationException("Ya has valorado esta compraventa.");
            }

            var valoracion = new Valoracion
            {
                IdCompraventa = idCompraventa,
                IdValorador = idComprador,
                IdValorado = compraventa.IdVendedor,
                RolValorado = "VENDEDOR",
                Puntuacion = puntuacion,
                Comentario = comentario,
                FechaCreacion = DateTime.UtcNow
            };

            await _repositorio.AddAsync(valoracion);

            var evento = new ValoracionCreadaEvent
            {
                IdentificadorEntidad = valoracion.Id.ToString(),
                TipoEvento = "valoracion-creada",
                FechaHora = DateTime.UtcNow,
                IdUsuarioValorado = valoracion.IdValorado,
                RolUsuarioValorado = valoracion.RolValorado,
                Puntuacion = valoracion.Puntuacion
            };
            _eventPublisher.PublishValoracionCreada(evento);

            return valoracion;
        }

        public async Task<Valoracion> RegistrarValoracionCompradorAsync(string idCompraventa, string idVendedor, int puntuacion, string? comentario)
        {
            if (puntuacion < 1 || puntuacion > 5)
            {
                throw new ArgumentException("La puntuación debe estar entre 1 y 5.");
            }

            var compraventa = await _compraventasClient.ObtenerCompraventaAsync(idCompraventa);
            if (compraventa == null)
            {
                throw new Exception("Compraventa no encontrada.");
            }

            if (compraventa.IdVendedor != idVendedor)
            {
                throw new UnauthorizedAccessException("El usuario no es el vendedor de esta compraventa.");
            }

            var existing = await _repositorio.GetByCompraventaAndValoradorAsync(idCompraventa, idVendedor);
            if (existing != null)
            {
                throw new InvalidOperationException("Ya has valorado esta compraventa.");
            }

            var valoracion = new Valoracion
            {
                IdCompraventa = idCompraventa,
                IdValorador = idVendedor,
                IdValorado = compraventa.IdComprador,
                RolValorado = "COMPRADOR",
                Puntuacion = puntuacion,
                Comentario = comentario,
                FechaCreacion = DateTime.UtcNow
            };

            await _repositorio.AddAsync(valoracion);

            var evento = new ValoracionCreadaEvent
            {
                IdentificadorEntidad = valoracion.Id.ToString(),
                TipoEvento = "valoracion-creada",
                FechaHora = DateTime.UtcNow,
                IdUsuarioValorado = valoracion.IdValorado,
                RolUsuarioValorado = valoracion.RolValorado,
                Puntuacion = valoracion.Puntuacion
            };
            _eventPublisher.PublishValoracionCreada(evento);

            return valoracion;
        }

        public async Task<(IEnumerable<Valoracion> Items, int TotalCount)> ConsultarValoracionesComoVendedorAsync(string idUsuario, int page, int size)
        {
            return await _repositorio.GetByUsuarioAndRolAsync(idUsuario, "VENDEDOR", page, size);
        }

        public async Task<(IEnumerable<Valoracion> Items, int TotalCount)> ConsultarValoracionesComoCompradorAsync(string idUsuario, int page, int size)
        {
            return await _repositorio.GetByUsuarioAndRolAsync(idUsuario, "COMPRADOR", page, size);
        }

        public async Task<Valoracion?> RecuperarValoracionAsync(int id)
        {
            return await _repositorio.GetByIdAsync(id);
        }
    }
}
