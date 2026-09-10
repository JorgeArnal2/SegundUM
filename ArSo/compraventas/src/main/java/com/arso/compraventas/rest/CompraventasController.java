package com.arso.compraventas.rest;

import com.arso.compraventas.domain.Compraventa;
import com.arso.compraventas.dto.CompraventaDto;
import com.arso.compraventas.dto.CrearCompraventaRequest;
import com.arso.compraventas.service.ServicioCompraventas;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.Link.of;
import static com.arso.compraventas.security.AuthorizationUtils.requireRole;
import static com.arso.compraventas.security.AuthorizationUtils.requireSubject;

@Tag(name = "Compraventas", description = "Registro y consulta de transacciones entre usuarios")
@RestController
@Validated
@RequestMapping(value = "/compraventas", produces = MediaType.APPLICATION_JSON_VALUE)
public class CompraventasController {

    private final ServicioCompraventas servicio;

    public CompraventasController(ServicioCompraventas servicio) {
        this.servicio = servicio;
    }

    // ──────────────────────────────────────────────────────────────────────
    // POST /compraventas
    // ──────────────────────────────────────────────────────────────────────
    @Operation(summary = "Registrar una compraventa",
               description = "El comprador adquiere el producto indicado. Se consultan los microservicios Productos y Usuarios para completar la información.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Compraventa registrada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
                     content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "404", description = "Producto o usuario no encontrado",
                     content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> crearCompraventa(@Valid @RequestBody CrearCompraventaRequest request,
                                                 Authentication authentication) {
        requireRole(authentication, "USUARIO");
        requireSubject(authentication, request.getIdComprador());
        Compraventa compraventa = servicio.crearCompraventa(request.getIdProducto(), request.getIdComprador());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(compraventa.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    // ──────────────────────────────────────────────────────────────────────
    // GET /compraventas/{id}
    // ──────────────────────────────────────────────────────────────────────
    @Operation(summary = "Obtener detalle de una compraventa", description = "Operación pública para que Valoraciones pueda consultarlo")
    @ApiResponse(responseCode = "200", description = "Compraventa obtenida")
    @ApiResponse(responseCode = "404", description = "Compraventa no encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CompraventaDto>> getCompraventa(@PathVariable String id) {
        Compraventa compraventa = servicio.getCompraventa(id);
        CompraventaDto dto = CompraventaDto.fromEntity(compraventa);
        Link self = of(ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString()).withSelfRel();
        return ResponseEntity.ok(EntityModel.of(dto, self));
    }

    // ──────────────────────────────────────────────────────────────────────
    // GET /compraventas/comprador/{idComprador}
    // ──────────────────────────────────────────────────────────────────────
    @Operation(summary = "Obtener compras de un usuario")
    @ApiResponse(responseCode = "200", description = "Lista paginada de compras")
    @GetMapping("/comprador/{idComprador}")
    public ResponseEntity<PagedModel<EntityModel<CompraventaDto>>> getComprasPorComprador(
            @Parameter(description = "Identificador del comprador") @PathVariable String idComprador,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            Authentication authentication) {

        requireRole(authentication, "USUARIO");
        requireSubject(authentication, idComprador);
        Page<Compraventa> resultPage = servicio.getComprasPorComprador(
                idComprador, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fecha")));
        return ResponseEntity.ok(toPagedModel(resultPage, page, size));
    }

    // ──────────────────────────────────────────────────────────────────────
    // GET /compraventas/vendedor/{idVendedor}
    // ──────────────────────────────────────────────────────────────────────
    @Operation(summary = "Obtener ventas de un usuario")
    @ApiResponse(responseCode = "200", description = "Lista paginada de ventas")
    @GetMapping("/vendedor/{idVendedor}")
    public ResponseEntity<PagedModel<EntityModel<CompraventaDto>>> getVentasPorVendedor(
            @Parameter(description = "Identificador del vendedor") @PathVariable String idVendedor,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            Authentication authentication) {

        requireRole(authentication, "USUARIO");
        requireSubject(authentication, idVendedor);
        Page<Compraventa> resultPage = servicio.getVentasPorVendedor(
                idVendedor, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fecha")));
        return ResponseEntity.ok(toPagedModel(resultPage, page, size));
    }

    // ──────────────────────────────────────────────────────────────────────
    // GET /compraventas?idComprador=...&idVendedor=...
    // ──────────────────────────────────────────────────────────────────────
    @Operation(summary = "Obtener compraventas entre un comprador y un vendedor concretos")
    @ApiResponse(responseCode = "200", description = "Lista paginada de compraventas")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<CompraventaDto>>> getCompraventasPorCompradorYVendedor(
            @Parameter(description = "Identificador del comprador", required = true) @RequestParam String idComprador,
            @Parameter(description = "Identificador del vendedor", required = true) @RequestParam String idVendedor,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            Authentication authentication) {

        requireRole(authentication, "ADMINISTRADOR");
        Page<Compraventa> resultPage = servicio.getCompraventasPorCompradorYVendedor(
                idComprador, idVendedor, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fecha")));
        return ResponseEntity.ok(toPagedModel(resultPage, page, size));
    }

    // ──────────────────────────────────────────────────────────────────────
    // Helpers de paginación HATEOAS
    // ──────────────────────────────────────────────────────────────────────
    private PagedModel<EntityModel<CompraventaDto>> toPagedModel(Page<Compraventa> source, int page, int size) {
        List<EntityModel<CompraventaDto>> content = source.getContent().stream()
                .map(c -> {
                    CompraventaDto dto = CompraventaDto.fromEntity(c);
                    Link self = of(ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/compraventas/{id}")
                            .buildAndExpand(c.getId())
                            .toUriString()).withSelfRel();
                    return EntityModel.of(dto, self);
                })
                .collect(Collectors.toList());

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                source.getSize(), source.getNumber(), source.getTotalElements(), source.getTotalPages());
        PagedModel<EntityModel<CompraventaDto>> model = PagedModel.of(content, metadata);

        model.add(of(ServletUriComponentsBuilder.fromCurrentRequest()
                .replaceQueryParam("page", page)
                .replaceQueryParam("size", size)
                .build().toUriString()).withSelfRel());

        if (source.hasPrevious()) {
            model.add(of(ServletUriComponentsBuilder.fromCurrentRequest()
                    .replaceQueryParam("page", page - 1)
                    .replaceQueryParam("size", size)
                    .build().toUriString()).withRel("prev"));
        }
        if (source.hasNext()) {
            model.add(of(ServletUriComponentsBuilder.fromCurrentRequest()
                    .replaceQueryParam("page", page + 1)
                    .replaceQueryParam("size", size)
                    .build().toUriString()).withRel("next"));
        }
        return model;
    }
}
