package com.valenci.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DtoRespuestaPedido {
    private int idPedido;
    private LocalDateTime fechaPedido;
    private String estadoPedido; // String para compatibilidad directa con React
    private String nombreCliente;
    private BigDecimal totalPedido;
    private List<DtoRespuestaDetallePedido> detalles;
}