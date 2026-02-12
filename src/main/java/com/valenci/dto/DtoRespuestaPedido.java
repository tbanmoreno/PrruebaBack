package com.valenci.dto;

import com.valenci.entidades.EstadoPedido;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DtoRespuestaPedido {
    private int idPedido;
    private LocalDateTime fechaPedido;
    private String estadoPedido; // Cambiado a String para facilitar el mapeo en React
    private String nombreCliente; // Esta es la propiedad clave
    private BigDecimal totalPedido;
    private List<DtoRespuestaDetallePedido> detalles;
}