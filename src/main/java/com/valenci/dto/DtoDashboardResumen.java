package com.valenci.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor // Necesario para la serialización de Jackson
public class DtoDashboardResumen {
    private BigDecimal ventasTotales;
    private long totalPedidos;
    private Map<String, Long> pedidosPorEstado;
    private List<DtoRespuestaProducto> productosStockCritico;
    private List<Map<String, Object>> topProductos;
}