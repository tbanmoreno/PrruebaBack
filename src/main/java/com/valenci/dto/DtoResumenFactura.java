package com.valenci.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoResumenFactura {
    private int idFactura;
    private LocalDateTime fechaFactura;
    private BigDecimal totalFactura;
    private BigDecimal iva;

    // CAMPOS ADICIONALES PARA EL FRONTEND
    private String nombreCliente;
    private String correoCliente;
    private List<DtoRespuestaDetallePedido> detalles = new ArrayList<>();

    // Constructor básico para compatibilidad con el Mapeador
    public DtoResumenFactura(int idFactura, LocalDateTime fechaFactura, BigDecimal totalFactura, BigDecimal iva) {
        this.idFactura = idFactura;
        this.fechaFactura = fechaFactura;
        this.totalFactura = totalFactura;
        this.iva = iva;
        this.detalles = new ArrayList<>(); // Garantiza coherencia inicial
    }
}