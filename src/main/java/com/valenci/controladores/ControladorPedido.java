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
// CORRECCIÓN CLAVE: Eliminamos el origen fijo para que tome la configuración global de SecurityConfig.
// Esto permite que el frontend de Vercel acceda sin bloqueos de CORS.
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

        // Manejo de flujos de servicio asegurando que no se devuelvan nulos al stream
        if (estado != null) pedidos = servicioPedido.listarPorEstado(estado);
        else if (fecha != null) pedidos = servicioPedido.listarPorFecha(fecha);
        else if (idProducto != null) pedidos = servicioPedido.listarPorProducto(idProducto);
        else pedidos = servicioPedido.listarTodos();

        if (pedidos == null) {
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }

        // El mapeador blindado se encarga de prevenir el error 500 si hay datos nulos en la DB
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
            // Limpieza de caracteres y conversión segura al Enum
            EstadoPedido estado = EstadoPedido.valueOf(nuevoEstadoStr.replace("\"", "").toUpperCase().trim());
            servicioPedido.actualizarEstado(id, estado);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado no válido: " + nuevoEstadoStr);
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENTE')")
    public ResponseEntity<DtoRespuestaPedido> crearPedidoDesdeCarrito(
            @AuthenticationPrincipal Usuario usuarioAutenticado,
            @Valid @RequestBody DtoSolicitudPedido dto) {
        try {
            Pedido nuevoPedido = new Pedido();

            // Verificación de instancia para asegurar que el cliente esté presente
            if (usuarioAutenticado instanceof Cliente cliente) {
                nuevoPedido.setCliente(cliente);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Perfil no autorizado para compras.");
            }

            // Mapeo manual de detalles asegurando la relación bidireccional
            List<DetallePedido> detalles = dto.getDetalles().stream().map(d -> {
                DetallePedido detalle = new DetallePedido();
                Producto p = new Producto();
                p.setIdProducto(d.getIdProducto());
                detalle.setProducto(p);
                detalle.setCantidad(d.getCantidad());
                detalle.setPedido(nuevoPedido); // Crucial para la persistencia en cascada
                return detalle;
            }).collect(Collectors.toList());

            nuevoPedido.setDetalles(detalles);

            // Persistencia y registro de pago en una sola transacción lógica
            Pedido creado = servicioPedido.crear(nuevoPedido);
            servicioPedido.registrarPago(creado.getIdPedido(), dto.getMetodoPago());

            return new ResponseEntity<>(MapeadorPedido.aDtoRespuesta(creado), HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno: " + e.getMessage());
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