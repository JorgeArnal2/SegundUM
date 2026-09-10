using System.Text;
using System.Text.Json;
using RabbitMQ.Client;
using valoraciones.Core.Ports.Out;
using valoraciones.Events;

namespace valoraciones.Infrastructure.Adapters.Out.Messaging
{
    public class RabbitMqEventPublisherAdapter : IEventPublisher, IDisposable
    {
        private readonly IConnection _connection;
        private readonly IModel _channel;
        private const string ExchangeName = "bus";

        public RabbitMqEventPublisherAdapter(IConfiguration configuration)
        {
            var factory = new ConnectionFactory()
            {
                Uri = new Uri(configuration["RabbitMQ:Uri"] ?? "amqp://guest:guest@localhost:5672/")
            };

            _connection = factory.CreateConnection();
            _channel = _connection.CreateModel();
            _channel.ExchangeDeclare(exchange: ExchangeName, type: "topic", durable: true);
        }

        public void PublishValoracionCreada(ValoracionCreadaEvent evento)
        {
            var routingKey = "bus.valoraciones.valoracion-creada";
            var options = new JsonSerializerOptions { PropertyNamingPolicy = JsonNamingPolicy.CamelCase };
            var message = JsonSerializer.Serialize(evento, options);
            var body = Encoding.UTF8.GetBytes(message);

            var properties = _channel.CreateBasicProperties();
            properties.Persistent = true;

            _channel.BasicPublish(
                exchange: ExchangeName,
                routingKey: routingKey,
                basicProperties: properties,
                body: body);
        }

        public void Dispose()
        {
            _channel?.Close();
            _connection?.Close();
        }
    }
}
