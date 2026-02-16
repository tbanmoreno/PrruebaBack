package com.valenci.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoRespuestaProducto {
    private int id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private int cantidad;
    private String nombreProveedor;
    private String imagen; // Nuevo campo para el Frontend
}