package com.arso.usuarios.port;

import com.arso.usuarios.event.UsuarioCreadoEvent;
import com.arso.usuarios.event.UsuarioModificadoEvent;

public interface EventPublisherPort {

    void publicarUsuarioCreado(UsuarioCreadoEvent event);

    void publicarUsuarioModificado(UsuarioModificadoEvent event);
}
