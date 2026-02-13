package com.valenci.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DtoPedidoHistorial {
    private int idPedido;
    private LocalDateTime fechaPedido;
    private BigDecimal totalPedido;
    private String estadoPedido; // String para consistencia JSON
    private DtoResumenFactura factura;
    private List<DtoRespuestaDetallePedido> detalles;
}