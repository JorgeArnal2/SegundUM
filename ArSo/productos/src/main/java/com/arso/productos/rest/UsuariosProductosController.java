package com.arso.productos.rest;

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
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.Link.of;

@Tag(name = "Productos por usuario", description = "Operaciones sobre los productos publicados por un vendedor")
@RestController
@Validated
@RequestMapping(value = "/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
public class UsuariosProductosController {

    private final ServicioProductos servicio;

    @Autowired
    public UsuariosProductosController(ServicioProductos servicio) {
        this.servicio = servicio;
    }

    @Operation(summary = "Obtener productos de un vendedor",
               description = "Devuelve la lista paginada de productos publicados por el usuario indicado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
        @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos",
                     content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}/productos")
    public ResponseEntity<PagedModel<EntityModel<ProductoDto>>> getProductosPorVendedor(
            @Parameter(description = "Identificador del vendedor") @PathVariable("id") String idVendedor,
            @Parameter(description = "Número de página (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {

        List<ProductoDto> productos = servicio.getProductosPorVendedor(idVendedor)
                .stream()
                .map(ProductoDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(toPagedModel(productos, page, size));
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
                .build().toUriString()).withSelfRel());

        if (page > 0) {
            model.add(of(ServletUriComponentsBuilder.fromCurrentRequest()
                    .replaceQueryParam("page", page - 1)
                    .replaceQueryParam("size", size)
                    .build().toUriString()).withRel("prev"));
        }
        if (page + 1 < totalPages) {
            model.add(of(ServletUriComponentsBuilder.fromCurrentRequest()
                    .replaceQueryParam("page", page + 1)
                    .replaceQueryParam("size", size)
                    .build().toUriString()).withRel("next"));
        }
        return model;
    }
}
