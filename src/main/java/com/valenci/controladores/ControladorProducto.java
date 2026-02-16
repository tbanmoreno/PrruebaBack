package com.valenci.controladores;

import com.valenci.dto.DtoRespuestaProducto;
import com.valenci.dto.DtoSolicitudProducto;
import com.valenci.entidades.Producto;
import com.valenci.entidades.Proveedor;
import com.valenci.mapper.MapeadorProducto;
import com.valenci.servicios.ServicioProducto;
import com.valenci.servicios.ServicioUsuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ControladorProducto {

    private final ServicioProducto servicioProducto;
    private final ServicioUsuario servicioUsuario;

    @GetMapping
    public ResponseEntity<List<DtoRespuestaProducto>> listarTodos() {
        // El mapeador ahora incluye el campo imagen en DtoRespuestaProducto
        List<Producto> productos = servicioProducto.listarTodos();
        return ResponseEntity.ok(MapeadorProducto.aListaDto(productos));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<DtoRespuestaProducto> crear(@Valid @RequestBody DtoSolicitudProducto dto) {
        Producto nuevoProducto = MapeadorProducto.aEntidad(dto);

        // ASIGNACIÓN DE IMAGEN: El DTO ya trae el Base64 en el campo imagen
        nuevoProducto.setImagen(dto.getImagen());

        var usuario = servicioUsuario.buscarPorId(dto.getIdProveedor())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado"));

        if (!(usuario instanceof Proveedor)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID no pertenece a un Proveedor");
        }

        nuevoProducto.setProveedor((Proveedor) usuario);
        Producto productoGuardado = servicioProducto.crear(nuevoProducto);

        return new ResponseEntity<>(MapeadorProducto.aDto(productoGuardado), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<DtoRespuestaProducto> actualizar(@PathVariable int id, @Valid @RequestBody DtoSolicitudProducto dto) {
        Producto datosNuevos = MapeadorProducto.aEntidad(dto);
        datosNuevos.setImagen(dto.getImagen()); // Actualización opcional de imagen

        Proveedor proveedor = (Proveedor) servicioUsuario.buscarPorId(dto.getIdProveedor())
                .filter(u -> u instanceof Proveedor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Proveedor no válido"));

        datosNuevos.setProveedor(proveedor);
        Producto actualizado = servicioProducto.actualizar(id, datosNuevos);

        return ResponseEntity.ok(MapeadorProducto.aDto(actualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        servicioProducto.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<DtoRespuestaProducto> actualizarStock(@PathVariable int id, @RequestBody Map<String, Integer> body) {
        if (!body.containsKey("cantidad")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta 'cantidad'");

        Producto producto = servicioProducto.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no hallado"));

        producto.setCantidad(body.get("cantidad"));
        Producto actualizado = servicioProducto.actualizar(id, producto);
        return ResponseEntity.ok(MapeadorProducto.aDto(actualizado));
    }
}