using Microsoft.AspNetCore.Mvc;
using valoraciones.Core.Domain;
using valoraciones.Core.Ports.In;
using valoraciones.DTOs;

namespace valoraciones.Infrastructure.Adapters.In.Web
{
    public static class ValoracionesEndpoints
    {
        public static void MapValoracionesEndpoints(this WebApplication app)
        {
            var group = app.MapGroup("/api/valoraciones");

            group.MapPost("/vendedor", RegistrarValoracionVendedor)
                .Produces<ValoracionResponseDto>(StatusCodes.Status201Created)
                .Produces(StatusCodes.Status400BadRequest)
                .Produces(StatusCodes.Status403Forbidden)
                .WithOpenApi();

            group.MapPost("/comprador", RegistrarValoracionComprador)
                .Produces<ValoracionResponseDto>(StatusCodes.Status201Created)
                .Produces(StatusCodes.Status400BadRequest)
                .Produces(StatusCodes.Status403Forbidden)
                .WithOpenApi();

            group.MapGet("/vendedor/{idUsuario}", ConsultarValoracionesComoVendedor)
                .Produces<PagedResponseDto<ValoracionResponseDto>>(StatusCodes.Status200OK)
                .WithOpenApi();

            group.MapGet("/comprador/{idUsuario}", ConsultarValoracionesComoComprador)
                .Produces<PagedResponseDto<ValoracionResponseDto>>(StatusCodes.Status200OK)
                .WithOpenApi();

            group.MapGet("/{id}", RecuperarValoracion).WithName("GetValoracion")
                .Produces<ValoracionResponseDto>(StatusCodes.Status200OK)
                .Produces(StatusCodes.Status404NotFound)
                .WithOpenApi();
        }

        private static async Task<IResult> RegistrarValoracionVendedor(CreateValoracionDto dto, IServicioValoraciones servicio)
        {
            var valoracion = await servicio.RegistrarValoracionVendedorAsync(
                dto.IdCompraventa, 
                dto.IdUsuarioRegistra, 
                dto.Puntuacion, 
                dto.Comentario);
                
            var responseDto = MapToDto(valoracion);
            return Results.CreatedAtRoute("GetValoracion", new { id = valoracion.Id }, responseDto);
        }

        private static async Task<IResult> RegistrarValoracionComprador(CreateValoracionDto dto, IServicioValoraciones servicio)
        {
            var valoracion = await servicio.RegistrarValoracionCompradorAsync(
                dto.IdCompraventa, 
                dto.IdUsuarioRegistra, 
                dto.Puntuacion, 
                dto.Comentario);
                
            var responseDto = MapToDto(valoracion);
            return Results.CreatedAtRoute("GetValoracion", new { id = valoracion.Id }, responseDto);
        }

        private static async Task<IResult> ConsultarValoracionesComoVendedor(string idUsuario, IServicioValoraciones servicio, HttpContext context, [FromQuery] int page = 1, [FromQuery] int size = 10)
        {
            var (items, totalCount) = await servicio.ConsultarValoracionesComoVendedorAsync(idUsuario, page, size);
            var response = CreatePagedResponse(items, totalCount, page, size, idUsuario, "vendedor", context);
            return Results.Ok(response);
        }

        private static async Task<IResult> ConsultarValoracionesComoComprador(string idUsuario, IServicioValoraciones servicio, HttpContext context, [FromQuery] int page = 1, [FromQuery] int size = 10)
        {
            var (items, totalCount) = await servicio.ConsultarValoracionesComoCompradorAsync(idUsuario, page, size);
            var response = CreatePagedResponse(items, totalCount, page, size, idUsuario, "comprador", context);
            return Results.Ok(response);
        }

        private static async Task<IResult> RecuperarValoracion(int id, IServicioValoraciones servicio)
        {
            var valoracion = await servicio.RecuperarValoracionAsync(id);
            if (valoracion == null) return Results.NotFound();
            
            var responseDto = MapToDto(valoracion);
            return Results.Ok(responseDto);
        }

        private static ValoracionResponseDto MapToDto(Valoracion valoracion)
        {
            var dto = new ValoracionResponseDto
            {
                Id = valoracion.Id,
                IdCompraventa = valoracion.IdCompraventa,
                IdValorador = valoracion.IdValorador,
                IdValorado = valoracion.IdValorado,
                RolValorado = valoracion.RolValorado,
                Puntuacion = valoracion.Puntuacion,
                Comentario = valoracion.Comentario,
                FechaCreacion = valoracion.FechaCreacion,
            };
            dto.Links.Add(new LinkDto("self", $"/api/valoraciones/{valoracion.Id}"));
            return dto;
        }

        private static PagedResponseDto<ValoracionResponseDto> CreatePagedResponse(
            IEnumerable<Valoracion> items, int totalCount, int page, int size, string idUsuario, string rolPath, HttpContext context)
        {
            var pagedResponse = new PagedResponseDto<ValoracionResponseDto>
            {
                Items = items.Select(MapToDto),
                TotalItems = totalCount,
                Page = page,
                Size = size
            };

            var baseUrl = $"{context.Request.Scheme}://{context.Request.Host}{context.Request.PathBase}/api/valoraciones/{rolPath}/{idUsuario}";

            pagedResponse.Links.Add(new LinkDto("self", $"{baseUrl}?page={page}&size={size}"));
            if (page > 1)
            {
                pagedResponse.Links.Add(new LinkDto("prev", $"{baseUrl}?page={page - 1}&size={size}"));
            }
            if (page * size < totalCount)
            {
                pagedResponse.Links.Add(new LinkDto("next", $"{baseUrl}?page={page + 1}&size={size}"));
            }

            return pagedResponse;
        }
    }
}
