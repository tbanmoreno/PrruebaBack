package com.valenci.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class DtoDashboardResumen {
    private BigDecimal ventasTotales;
    private long totalPedidos;
    private Map<String, Long> pedidosPorEstado;
    private List<DtoRespuestaProducto> productosStockCritico;
    private List<Map<String, Object>> topProductos; // Nombre y cantidad vendida
}