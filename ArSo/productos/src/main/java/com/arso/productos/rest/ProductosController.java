package com.arso.productos.rest;


import com.arso.productos.domain.EstadoProducto;
import com.arso.productos.domain.ProductoResumen;
import com.arso.productos.dto.CrearProductoRequest;
import com.arso.productos.dto.LugarRecogidaRequest;
import com.arso.productos.dto.ModificarProductoRequest;

import com.arso.productos.domain.Producto;
import com.arso.productos.dto.ProductoDto;
import com.arso.productos.service.ServicioProductos;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.Link.of;
import static com.arso.productos.security.AuthorizationUtils.requireRole;
import static com.arso.productos.security.AuthorizationUtils.requireSubject;

@Tag(name = "Productos", description = "Gestión del catálogo de productos")
@RestController
@Validated
@RequestMapping(value = "/productos", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductosController {
    private ServicioProductos servicio;

    @Autowired
    public ProductosController(ServicioProductos servicio){
        this.servicio = servicio;
    }

    @Operation(summary = "Publicar un nuevo producto")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Producto creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
                     content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createProducto(@Valid @RequestBody CrearProductoRequest dto,
                                               Authentication authentication) {
        requireRole(authentication, "USUARIO");
        requireSubject(authentication, dto.getIdVendedor());
        String id = this.servicio.altaProducto(
                dto.getTitulo(),
                dto.getDescripcion(),
                dto.getPrecio(),
                dto.getEstado(),
                dto.getIdCategoria(),
                dto.getEnvioDisponible(),
                dto.getIdVendedor()
        );
        URI nuevaURL = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(id).toUri();

        return ResponseEntity.created(nuevaURL).build();
    }

    @Operation(summary = "Obtener un producto por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                     content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDto> getProducto(
            @Parameter(description = "Identificador del producto") @PathVariable String id) {
        Producto producto = this.servicio.getProductoPorId(id);
        return ResponseEntity.ok(ProductoDto.fromEntity(producto));
    }

    @Operation(summary = "Buscar productos con filtros opcionales y paginación")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado paginado de productos"),
        @ApiResponse(responseCode = "400", description = "Parámetros inválidos",
                     content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ProductoDto>>> buscarProductos(
            @Parameter(description = "Filtrar por categoría") @RequestParam(required = false) String categoria,
            @Parameter(description = "Filtrar por texto") @RequestParam(required = false) String texto,
            @Parameter(description = "Filtrar por estado") @RequestParam(required = false) EstadoProducto estado,
            @Parameter(description = "Precio máximo") @RequestParam(required = false) Double precioMaximo,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {

        List<ProductoDto> productos = this.servicio.buscarProductos(categoria, texto, estado, precioMaximo)
                .stream()
                .map(ProductoDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(toPagedModel(productos, page, size));
    }

    @Operation(summary = "Modificar precio y descripción de un producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Modificación realizada"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                     content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
                     content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modificarProducto(
            @Parameter(description = "Identificador del producto") @PathVariable String id,
            @Valid @RequestBody ModificarProductoRequest request,
            Authentication authentication) {
        Producto producto = this.servicio.getProductoPorId(id);
        requireRole(authentication, "USUARIO");
        requireSubject(authentication, producto.getVendedor().getId());
        this.servicio.modificarProducto(id, request.getDescripcion(), request.getPrecio());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Registrar una visualización en un producto")
    @ApiResponse(responseCode = "201", description = "Visualización registrada")
    @PostMapping("/{id}/visualizaciones")
    public ResponseEntity<Void> anadirVisualizacion(
            @Parameter(description = "Identificador del producto") @PathVariable String id) {
        this.servicio.anadirVisualizacion(id);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Asignar lugar de recogida a un producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Recogida asignada"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                     content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping(value = "/{id}/recogida", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> asignarRecogida(
            @Parameter(description = "Identificador del producto") @PathVariable String id,
            @Valid @RequestBody LugarRecogidaRequest request,
            Authentication authentication) {
        Producto producto = this.servicio.getProductoPorId(id);
        requireRole(authentication, "USUARIO");
        requireSubject(authentication, producto.getVendedor().getId());
        this.servicio.asignarLugarRecogida(id, request.getDescripcion(), request.getLongitud(), request.getLatitud());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Productos publicados por un vendedor")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado paginado de productos"),
        @ApiResponse(responseCode = "400", description = "Parámetros inválidos",
                     content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/vendedor/{id}")
    public ResponseEntity<PagedModel<EntityModel<ProductoDto>>> getProductosPorVendedor(
            @Parameter(description = "Identificador del vendedor") @PathVariable("id") String idVendedor,
            @Parameter(description = "Número de página (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {

        List<ProductoDto> productos = this.servicio.getProductosPorVendedor(idVendedor)
                .stream()
                .map(ProductoDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(toPagedModel(productos, page, size));
    }

    @Operation(summary = "Historial de productos de un mes/año concreto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Historial paginado"),
        @ApiResponse(responseCode = "400", description = "Parámetros inválidos",
                     content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/historial")
    public ResponseEntity<PagedModel<EntityModel<ProductoResumen>>> historialMes(
            @Parameter(description = "Mes (1-12)") @RequestParam @Min(1) @Max(12) int mes,
            @Parameter(description = "Año") @RequestParam @Min(2000) int anio,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        List<ProductoResumen> resumenes = this.servicio.getHistorialMes(mes, anio);
        return ResponseEntity.ok(toPagedModelResumen(resumenes, page, size));
    }

    private PagedModel<EntityModel<ProductoDto>> toPagedModel(List<ProductoDto> source, int page, int size) {
        int totalElements = source.size();
        int fromIndex = Math.min(page * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<EntityModel<ProductoDto>> content = new ArrayList<>();
        for (ProductoDto dto : source.subList(fromIndex, toIndex)) {
            Link self = of(ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/productos/{id}")
                    .buildAndExpand(dto.getId())
                    .toUriString()).withSelfRel();
            content.add(EntityModel.of(dto, self));
        }

        int totalPages = totalElements == 0 ? 1 : (int) Math.ceil((double) totalElements / size);
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(size, page, totalElements, totalPages);
        PagedModel<EntityModel<ProductoDto>> model = PagedModel.of(content, metadata);

        model.add(of(ServletUriComponentsBuilder.fromCurrentRequest()
                .replaceQueryParam("page", page)
                .replaceQueryParam("size", size)
                .build()
                .toUriString()).withSelfRel());

        if (page > 0) {
            model.add(of(ServletUriComponentsBuilder.fromCurrentRequest()
                    .replaceQueryParam("page", page - 1)
                    .replaceQueryParam("size", size)
                    .build()
                    .toUriString()).withRel("prev"));
        }

        if (page + 1 < totalPages) {
            model.add(of(ServletUriComponentsBuilder.fromCurrentRequest()
                    .replaceQueryParam("page", page + 1)
                    .replaceQueryParam("size", size)
                    .build()
                    .toUriString()).withRel("next"));
        }
        return model;
    }

    private PagedModel<EntityModel<ProductoResumen>> toPagedModelResumen(List<ProductoResumen> source, int page, int size) {
        int totalElements = source.size();
        int fromIndex = Math.min(page * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);
        List<EntityModel<ProductoResumen>> content = source.subList(fromIndex, toIndex)
                .stream()
                .map(EntityModel::of)
                .collect(Collectors.toList());

        int totalPages = totalElements == 0 ? 1 : (int) Math.ceil((double) totalElements / size);
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(size, page, totalElements, totalPages);
        PagedModel<EntityModel<ProductoResumen>> model = PagedModel.of(content, metadata);

        model.add(of(ServletUriComponentsBuilder.fromCurrentRequest()
                .replaceQueryParam("page", page)
                .replaceQueryParam("size", size)
                .build()
                .toUriString()).withSelfRel());

        if (page > 0) {
            model.add(of(ServletUriComponentsBuilder.fromCurrentRequest()
                    .replaceQueryParam("page", page - 1)
                    .replaceQueryParam("size", size)
                    .build()
                    .toUriString()).withRel("prev"));
        }
        if (page + 1 < totalPages) {
            model.add(of(ServletUriComponentsBuilder.fromCurrentRequest()
                    .replaceQueryParam("page", page + 1)
                    .replaceQueryParam("size", size)
                    .build()
                    .toUriString()).withRel("next"));
        }
        return model;
    }

}
