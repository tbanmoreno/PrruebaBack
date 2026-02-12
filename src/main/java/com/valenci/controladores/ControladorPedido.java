package com.valenci.controladores;

import com.valenci.dto.DtoRespuestaPedido;
import com.valenci.dto.DtoSolicitudPedido;
import com.valenci.entidades.*;
import com.valenci.mapper.MapeadorPedido;
import com.valenci.servicios.ServicioPedido;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:5173")
public class ControladorPedido {

    private final ServicioPedido servicioPedido;

    public ControladorPedido(ServicioPedido servicioPedido) {
        this.servicioPedido = servicioPedido;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<DtoRespuestaPedido>> listarConFiltros(
            @RequestParam(required = false) EstadoPedido estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) Integer idProducto) {

        List<Pedido> pedidos;
        if (estado != null) pedidos = servicioPedido.listarPorEstado(estado);
        else if (fecha != null) pedidos = servicioPedido.listarPorFecha(fecha);
        else if (idProducto != null) pedidos = servicioPedido.listarPorProducto(idProducto);
        else pedidos = servicioPedido.listarTodos();

        return ResponseEntity.ok(pedidos.stream()
                .map(MapeadorPedido::aDtoRespuesta)
                .collect(Collectors.toList()));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Void> actualizarEstado(
            @PathVariable int id,
            @RequestBody Map<String, String> body) {

        String nuevoEstadoStr = body.get("nuevoEstado");
        if (nuevoEstadoStr == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cuerpo debe contener 'nuevoEstado'");
        }

        try {
            EstadoPedido estado = EstadoPedido.valueOf(nuevoEstadoStr.replace("\"", "").toUpperCase());
            servicioPedido.actualizarEstado(id, estado);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado no válido: " + nuevoEstadoStr);
        }
    }

    @PostMapping
    // CORRECCIÓN: De hasRole('CLIENTE') a hasAuthority('CLIENTE')
    @PreAuthorize("hasAuthority('CLIENTE')")
    public ResponseEntity<DtoRespuestaPedido> crearPedidoDesdeCarrito(
            @AuthenticationPrincipal Usuario usuarioAutenticado,
            @Valid @RequestBody DtoSolicitudPedido dto) {
        try {
            Pedido nuevoPedido = new Pedido();
            if (usuarioAutenticado instanceof Cliente cliente) {
                nuevoPedido.setCliente(cliente);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo usuarios con perfil de Cliente pueden realizar compras.");
            }

            List<DetallePedido> detalles = dto.getDetalles().stream().map(d -> {
                DetallePedido detalle = new DetallePedido();
                Producto p = new Producto();
                p.setIdProducto(d.getIdProducto());
                detalle.setProducto(p);
                detalle.setCantidad(d.getCantidad());
                detalle.setPedido(nuevoPedido);
                return detalle;
            }).collect(Collectors.toList());

            nuevoPedido.setDetalles(detalles);

            Pedido creado = servicioPedido.crear(nuevoPedido);
            servicioPedido.registrarPago(creado.getIdPedido(), dto.getMetodoPago());

            return new ResponseEntity<>(MapeadorPedido.aDtoRespuesta(creado), HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al procesar el pedido: " + e.getMessage());
        }
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<DtoRespuestaPedido>> listarPorClienteId(@PathVariable int clienteId) {
        List<Pedido> pedidos = servicioPedido.listarPorCliente(clienteId);
        return ResponseEntity.ok(pedidos.stream()
                .map(MapeadorPedido::aDtoRespuesta)
                .collect(Collectors.toList()));
    }
}