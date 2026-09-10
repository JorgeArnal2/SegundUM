package com.arso.usuarios.rest;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import com.arso.auth.JwtTokenFilter;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api")
public class UsuariosApplication extends ResourceConfig {

    public UsuariosApplication() {
        packages("com.arso.usuarios.rest");
        register(JacksonFeature.class);
        register(JwtTokenFilter.class);
    }
}
