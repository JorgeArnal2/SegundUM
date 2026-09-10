using Microsoft.EntityFrameworkCore;
using valoraciones.Core.Ports.In;
using valoraciones.Core.Ports.Out;
using valoraciones.Core.Services;
using valoraciones.Infrastructure.Adapters.In.Web;
using valoraciones.Infrastructure.Adapters.Out.Database;
using valoraciones.Infrastructure.Adapters.Out.Http;
using valoraciones.Infrastructure.Adapters.Out.Messaging;
using valoraciones.Middleware;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// Configure Database
var connectionString = builder.Configuration.GetConnectionString("DefaultConnection") 
    ?? "Server=localhost;Port=3306;Database=segundum_valoraciones;User=root;Password=secret;";
builder.Services.AddDbContext<ValoracionesDbContext>(options =>
    options.UseMySql(connectionString, ServerVersion.AutoDetect(connectionString)));

// Configure HTTP Client Adapter
builder.Services.AddHttpClient<ICompraventasClient, CompraventasClientAdapter>(client =>
{
    var baseUrl = builder.Configuration["Microservicios:Compraventas"] ?? "http://localhost:8082/";
    client.BaseAddress = new Uri(baseUrl);
});

// Configure RabbitMQ Publisher Adapter
builder.Services.AddSingleton<IEventPublisher, RabbitMqEventPublisherAdapter>();

// Configure Repositories and Services
builder.Services.AddScoped<IRepositorioValoraciones, RepositorioValoraciones>();
builder.Services.AddScoped<IServicioValoraciones, ServicioValoraciones>();

var app = builder.Build();

// Migrate Database on startup
using (var scope = app.Services.CreateScope())
{
    var dbContext = scope.ServiceProvider.GetRequiredService<ValoracionesDbContext>();
    dbContext.Database.EnsureDeleted();
    dbContext.Database.EnsureCreated();
}

app.UseMiddleware<ExceptionMiddleware>();
app.UseSwagger();
app.UseSwaggerUI();

app.MapValoracionesEndpoints();

app.Run();
