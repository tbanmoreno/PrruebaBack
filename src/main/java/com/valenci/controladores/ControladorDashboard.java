package com.valenci.controladores;

import com.valenci.dto.DtoDashboardResumen;
import com.valenci.entidades.EstadoPedido;
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
        // 1. Obtener datos básicos de pedidos
        var pedidos = servicioPedido.listarTodos();

        // 2. Calcular Ventas Totales (Filtrando pedidos válidos)
        BigDecimal ventasTotales = pedidos.stream()
                .filter(p -> p.getEstadoPedido() != EstadoPedido.PENDIENTE && p.getEstadoPedido() != EstadoPedido.CANCELADO)
                .map(p -> p.getTotalPedido())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Conteo de pedidos por estado para gráficas
        Map<String, Long> pedidosPorEstado = pedidos.stream()
                .collect(Collectors.groupingBy(p -> p.getEstadoPedido().name(), Collectors.counting()));

        // 4. Stock Crítico usando tu Repositorio optimizado
        var stockCritico = servicioProducto.listarTodos().stream()
                .filter(p -> p.getCantidad() < 15)
                .map(MapeadorProducto::aDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new DtoDashboardResumen(
                ventasTotales,
                (long) pedidos.size(),
                pedidosPorEstado,
                stockCritico,
                null // Espacio para Top Productos en el futuro
        ));
    }
}