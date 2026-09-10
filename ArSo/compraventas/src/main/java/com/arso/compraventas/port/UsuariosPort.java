package com.arso.compraventas.port;

import com.arso.compraventas.port.dto.UsuarioRemotoDto;

public interface UsuariosPort {
    UsuarioRemotoDto getNombreUsuario(String idUsuario);
}
