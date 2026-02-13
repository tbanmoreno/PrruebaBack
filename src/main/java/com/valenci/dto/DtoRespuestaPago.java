package com.valenci.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DtoRespuestaPago {
    private int idPago;
    private int idPedido;
    private String nombreCliente;
    private BigDecimal monto;
    private LocalDateTime fechaPago;
    private String metodoPago;
}
