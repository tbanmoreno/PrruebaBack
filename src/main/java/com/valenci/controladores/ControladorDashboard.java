package com.valenci.controladores;

import com.valenci.dto.DtoDashboardResumen;
import com.valenci.dto.DtoTopProducto;
import com.valenci.entidades.EstadoPedido;
import com.valenci.entidades.Pedido;
import com.valenci.entidades.DetallePedido;
import com.valenci.mapper.MapeadorProducto;
import com.valenci.servicios.ServicioPedido;
import com.valenci.servicios.ServicioProducto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class ControladorDashboard {

    private final ServicioPedido servicioPedido;
    private final ServicioProducto servicioProducto;

    @GetMapping("/resumen")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<DtoDashboardResumen> obtenerResumen() {
        List<Pedido> pedidos = servicioPedido.listarTodos();
        if (pedidos == null) pedidos = new ArrayList<>();

        // 1. Calcular Ventas Totales
        BigDecimal ventasTotales = pedidos.stream()
                .filter(p -> p.getEstadoPedido() != null &&
                        p.getEstadoPedido() != EstadoPedido.PENDIENTE &&
                        p.getEstadoPedido() != EstadoPedido.CANCELADO)
                .map(p -> p.getTotalPedido() != null ? p.getTotalPedido() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Conteo de pedidos por estado
        Map<String, Long> pedidosPorEstado = pedidos.stream()
                .filter(p -> p.getEstadoPedido() != null)
                .collect(Collectors.groupingBy(p -> p.getEstadoPedido().name(), Collectors.counting()));

        // 3. Lógica para Top Productos (Agrupación y Ordenación)
        List<DtoTopProducto> topProductos = pedidos.stream()
                .filter(p -> p.getEstadoPedido() != EstadoPedido.CANCELADO)
                .flatMap(p -> p.getDetalles() != null ? p.getDetalles().stream() : java.util.stream.Stream.empty())
                .filter(d -> d.getProducto() != null)
                .collect(Collectors.groupingBy(
                        d -> d.getProducto().getNombreProducto(),
                        Collectors.summingLong(DetallePedido::getCantidad)
                ))
                .entrySet().stream()
                .map(entry -> new DtoTopProducto(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> Long.compare(b.getCantidadVendida(), a.getCantidadVendida()))
                .limit(5)
                .collect(Collectors.toList());

        // 4. Stock Crítico
        var productos = servicioProducto.listarTodos();
        var stockCritico = (productos == null) ? new ArrayList<com.valenci.dto.DtoRespuestaProducto>() :
                productos.stream()
                        .filter(p -> p.getCantidad() < 15)
                        .map(MapeadorProducto::aDto)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(new DtoDashboardResumen(
                ventasTotales,
                (long) pedidos.size(),
                pedidosPorEstado.isEmpty() ? new HashMap<>() : pedidosPorEstado,
                stockCritico,
                topProductos
        ));
    }
}