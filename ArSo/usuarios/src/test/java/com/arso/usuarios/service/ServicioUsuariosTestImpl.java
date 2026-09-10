package com.arso.usuarios.service;

import com.arso.usuarios.event.UsuarioCreadoEvent;
import com.arso.usuarios.event.UsuarioModificadoEvent;
import com.arso.usuarios.port.EventPublisherPort;

public class ServicioUsuariosTestImpl extends ServicioUsuariosImpl {

    public ServicioUsuariosTestImpl() {
        super(new EventPublisherPort() {
            @Override
            public void publicarUsuarioCreado(UsuarioCreadoEvent event) {
            }

            @Override
            public void publicarUsuarioModificado(UsuarioModificadoEvent event) {
            }
        });
    }
}
