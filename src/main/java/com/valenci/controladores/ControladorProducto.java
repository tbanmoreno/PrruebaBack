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
@RequiredArgsConstructor // Simplifica los constructores
@CrossOrigin(origins = "http://localhost:5173")
public class ControladorProducto {

    private final ServicioProducto servicioProducto;
    private final ServicioUsuario servicioUsuario;

    @GetMapping
    public ResponseEntity<List<DtoRespuestaProducto>> listarTodos() {
        // Sugerencia Senior: El mapeo debería ocurrir idealmente antes de cerrar la transacción
        // o asegurar que el repositorio use un "JOIN FETCH"
        List<Producto> productos = servicioProducto.listarTodos();
        return ResponseEntity.ok(MapeadorProducto.aListaDto(productos));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<DtoRespuestaProducto> crear(@Valid @RequestBody DtoSolicitudProducto dto) {
        Producto nuevoProducto = MapeadorProducto.aEntidad(dto);

        // Mejora: Validación de tipo de usuario sin cast arriesgado
        var usuario = servicioUsuario.buscarPorId(dto.getIdProveedor())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado"));

        if (!(usuario instanceof Proveedor)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID proporcionado no pertenece a un Proveedor");
        }

        nuevoProducto.setProveedor((Proveedor) usuario);
        Producto productoGuardado = servicioProducto.crear(nuevoProducto);

        return new ResponseEntity<>(MapeadorProducto.aDto(productoGuardado), HttpStatus.CREATED);
    }

    // NUEVO: Método para actualizar (Necesario para tu Dashboard y Gestión)
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<DtoRespuestaProducto> actualizar(@PathVariable int id, @Valid @RequestBody DtoSolicitudProducto dto) {
        Producto datosNuevos = MapeadorProducto.aEntidad(dto);

        // Buscamos el proveedor para el producto actualizado
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
    public ResponseEntity<DtoRespuestaProducto> actualizarStock(
            @PathVariable int id,
            @RequestBody Map<String, Integer> body) {

        int nuevaCantidad = body.get("cantidad");
        Producto producto = servicioProducto.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        producto.setCantidad(nuevaCantidad);
        Producto actualizado = servicioProducto.actualizar(id, producto);

        return ResponseEntity.ok(MapeadorProducto.aDto(actualizado));
    }
}