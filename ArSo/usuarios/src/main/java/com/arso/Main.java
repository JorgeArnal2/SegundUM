package com.arso;

import com.arso.usuarios.bus.UsuarioCompraventaConsumer;
import com.arso.usuarios.rest.UsuariosApplication;
import com.arso.usuarios.service.ServicioUsuarios;
import com.arso.usuarios.service.EventosDominioUsuariosService;
import com.arso.service.FactoriaServicios;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import java.io.IOException;
import java.net.URI;

public class Main {

    private static final String HOST = System.getenv("SERVER_HOST") != null ? System.getenv("SERVER_HOST") : "0.0.0.0";
    private static final String BASE_URI = "http://" + HOST + ":8080/api/";

    public static void main(String[] args) throws IOException {
        ServicioUsuarios servicioUsuarios = FactoriaServicios.getServicio(ServicioUsuarios.class);
        EventosDominioUsuariosService eventosPort = new EventosDominioUsuariosService(servicioUsuarios);
        UsuarioCompraventaConsumer consumer = new UsuarioCompraventaConsumer(eventosPort);
        consumer.start();
        
        com.arso.usuarios.bus.UsuarioValoracionConsumer valoracionConsumer = new com.arso.usuarios.bus.UsuarioValoracionConsumer(eventosPort);
        valoracionConsumer.start();

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
            URI.create(BASE_URI),
            new UsuariosApplication()
        );

        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║  Microservicio Usuarios arrancado en " + BASE_URI + " ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║ ✓ Autenticación delegada en la pasarela             ║");
        System.out.println("║ ✓ Credenciales: POST " + BASE_URI + "usuarios/credenciales ║");
        System.out.println("║ ✓ RabbitMQ consumos habilitados                     ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║ El servidor se está ejecutando en segundo plano...   ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        consumer.close();
        valoracionConsumer.close();
        server.shutdownNow();
    }
}
