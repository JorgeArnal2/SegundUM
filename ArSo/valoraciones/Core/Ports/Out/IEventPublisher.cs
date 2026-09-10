using valoraciones.Events;

namespace valoraciones.Core.Ports.Out
{
    public interface IEventPublisher
    {
        void PublishValoracionCreada(ValoracionCreadaEvent evento);
    }
}
