package com.valenci.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DtoRespuestaFactura {
    private int idFactura;
    private LocalDateTime fecha;
    private String nombreCliente;
    private String correoCliente;
    private BigDecimal total;
    private BigDecimal iva;
    private List<DtoRespuestaDetallePedido> detalles; // Reutilizamos el DTO de detalles
}

