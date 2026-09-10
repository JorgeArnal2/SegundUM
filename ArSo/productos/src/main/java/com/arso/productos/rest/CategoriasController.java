package com.arso.productos.rest;

import com.arso.productos.dto.CategoriaDto;
import com.arso.productos.service.ServicioCategorias;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Categorias", description = "Consulta de categorias de productos")
@RestController
@RequestMapping(value = "/categorias", produces = MediaType.APPLICATION_JSON_VALUE)
public class CategoriasController {

    private final ServicioCategorias servicio;

    @Autowired
    public CategoriasController(ServicioCategorias servicio) {
        this.servicio = servicio;
    }

    @Operation(summary = "Listar categorias raiz")
    @ApiResponse(responseCode = "200", description = "Listado de categorias de primer nivel")
    @GetMapping
    public ResponseEntity<List<CategoriaDto>> getCategoriasRaiz() {
        return ResponseEntity.ok(servicio.getCategoriasRaiz());
    }

    @Operation(summary = "Obtener categoria con subcategorias directas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
        @ApiResponse(responseCode = "404", description = "Categoria no encontrada",
                     content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDto.CategoriaDetalleDto> getCategoria(
            @Parameter(description = "Identificador de la categoria") @PathVariable String id) {
        return ResponseEntity.ok(servicio.getCategoriaPorId(id));
    }
}
