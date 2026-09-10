package com.arso.usuarios.rest;

import com.arso.service.FactoriaServicios;
import com.arso.usuarios.dto.CredencialesDTO;
import com.arso.usuarios.dto.GithubLoginRequestDTO;
import com.arso.usuarios.dto.UsuarioAuthDTO;
import com.arso.usuarios.domain.Usuario;
import com.arso.usuarios.dto.UsuarioRequestDTO;
import com.arso.usuarios.dto.UsuarioResponseDTO;
import com.arso.usuarios.dto.UsuarioResumenDTO;
import com.arso.usuarios.service.ServicioUsuarios;
import io.jsonwebtoken.Claims;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.*;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    private final ServicioUsuarios servicio =
        FactoriaServicios.getServicio(ServicioUsuarios.class);

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @POST
    @PermitAll
    public Response crearUsuario(UsuarioRequestDTO dto, @Context UriInfo uriInfo) {
        Date fechaNacimiento = parseFecha(dto.getFechaNacimiento());

        String idUsuario = servicio.altaUsuario(
            dto.getNombre(),
            dto.getApellidos(),
            dto.getEmail(),
            dto.getClave(),
            fechaNacimiento,
            dto.getTelefono()
        );

        URI location = uriInfo.getAbsolutePathBuilder().path(idUsuario).build();
        return Response.created(location).build();
    }

    @POST
    @Path("/credenciales")
    @PermitAll
    public Response verificarCredenciales(CredencialesDTO dto) {
        if (dto == null || dto.getEmail() == null || dto.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("Email y password son obligatorios").build();
        }

        Usuario usuario = servicio.obtenerPorEmail(dto.getEmail());
        if (usuario == null || !usuario.getClave().equals(dto.getPassword())) {
            return Response.status(Response.Status.UNAUTHORIZED)
                .entity("Credenciales inválidas").build();
        }

        return Response.ok(UsuarioAuthDTO.fromUsuario(usuario)).build();
    }

    @POST
    @Path("/github")
    @PermitAll
    public Response loginGithub(GithubLoginRequestDTO dto) {
        if (dto == null || dto.getGithubId() == null || dto.getGithubId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("githubId es obligatorio").build();
        }

        Usuario usuario = servicio.obtenerPorGithubId(dto.getGithubId());
        if (usuario == null && dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            usuario = servicio.obtenerPorEmail(dto.getEmail());
            if (usuario != null) {
                String githubIdActual = usuario.getGithubId();
                if (githubIdActual == null || githubIdActual.trim().isEmpty()) {
                    servicio.actualizarGithubId(usuario.getId(), dto.getGithubId());
                    usuario.setGithubId(dto.getGithubId());
                } else if (!githubIdActual.equals(dto.getGithubId())) {
                    return Response.status(Response.Status.CONFLICT)
                        .entity("El usuario ya está vinculado a otro GitHub ID").build();
                }
            }
        }

        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("Usuario no encontrado").build();
        }

        return Response.ok(UsuarioAuthDTO.fromUsuario(usuario)).build();
    }

    @GET
    public Response listarUsuarios(@Context UriInfo uriInfo) {
        List<Usuario> usuarios = servicio.getUsuarios();
        List<UsuarioResumenDTO> resumen = usuarios.stream()
            .map(u -> UsuarioResumenDTO.fromUsuario(u, uriInfo))
            .collect(Collectors.toList());
        return Response.ok(resumen).build();
    }

    @GET
    @Path("/{id}")
    public Response obtenerUsuario(@PathParam("id") String id) {
        Usuario usuario = servicio.getUsuario(id);
        return Response.ok(UsuarioResponseDTO.fromUsuario(usuario)).build();
    }

    @GET
    @Path("/{id}/nombre")
    @PermitAll
    public Response obtenerNombreUsuario(@PathParam("id") String id) {
        Usuario usuario = servicio.getUsuario(id);
        java.util.Map<String, String> result = new java.util.LinkedHashMap<>();
        result.put("id", usuario.getId());
        result.put("nombre", usuario.getNombre());
        result.put("apellidos", usuario.getApellidos());
        return Response.ok(result).build();
    }

    @PUT
    @Path("/{id}")
    public Response modificarUsuario(@PathParam("id") String id, UsuarioRequestDTO dto, @Context ContainerRequestContext requestContext) {
        // Obtener usuario autenticado del token
        Claims claims = (Claims) requestContext.getProperty("claims");
        if (claims == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                .entity("Usuario no autenticado").build();
        }
        
        String usuarioAutenticado = claims.get("sub", String.class);
        String rolUsuario = claims.get("roles", String.class);

        // Validar autorización: solo el propietario o un ADMIN puede modificar
        boolean esAdmin = "ADMIN".equals(rolUsuario) || "ADMINISTRADOR".equals(rolUsuario);
        if (!usuarioAutenticado.equals(id) && !esAdmin) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity("No tienes permiso para modificar este usuario").build();
        }
        
        Date fechaNacimiento = parseFecha(dto.getFechaNacimiento());

        servicio.modificarUsuario(
            id,
            dto.getNombre(),
            dto.getApellidos(),
            dto.getClave(),
            fechaNacimiento,
            dto.getTelefono()
        );

        return Response.ok().build();
    }

    private Date parseFecha(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) {
            return null;
        }
        try {
            return DATE_FORMAT.parse(fecha);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Formato de fecha no válido. Use yyyy-MM-dd");
        }
    }
}
