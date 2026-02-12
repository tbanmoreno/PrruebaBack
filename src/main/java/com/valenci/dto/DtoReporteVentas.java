package com.valenci.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoReporteVentas {

    // Total de dinero recaudado
    private BigDecimal totalIngresos;

    // Cantidad total de pedidos procesados
    private Long cantidadPedidos;

    // El producto más vendido (opcional, para futura expansión)
    private String productoEstrella;
}