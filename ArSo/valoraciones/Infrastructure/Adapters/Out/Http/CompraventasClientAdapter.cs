using System.Text.Json;
using valoraciones.Core.Ports.Out;
using valoraciones.DTOs;

namespace valoraciones.Infrastructure.Adapters.Out.Http
{
    public class CompraventasClientAdapter : ICompraventasClient
    {
        private readonly HttpClient _httpClient;

        public CompraventasClientAdapter(HttpClient httpClient)
        {
            _httpClient = httpClient;
        }

        public async Task<CompraventaDto?> ObtenerCompraventaAsync(string idCompraventa)
        {
            var response = await _httpClient.GetAsync($"/compraventas/{idCompraventa}");
            if (!response.IsSuccessStatusCode)
            {
                return null;
            }

            var content = await response.Content.ReadAsStringAsync();
            var options = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };
            return JsonSerializer.Deserialize<CompraventaDto>(content, options);
        }
    }
}
