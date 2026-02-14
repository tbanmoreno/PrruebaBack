package com.valenci.mapper;

import com.valenci.dto.DtoRespuestaFactura;
import com.valenci.entidades.Factura;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class MapeadorFactura {

    public static DtoRespuestaFactura aDto(Factura entidad) {
        if (entidad == null) return null;

        DtoRespuestaFactura dto = new DtoRespuestaFactura();
        dto.setIdFactura(entidad.getIdFactura());
        dto.setFecha(entidad.getFechaFactura() != null ? entidad.getFechaFactura() : LocalDateTime.now());
        dto.setTotal(entidad.getTotalFactura() != null ? entidad.getTotalFactura() : BigDecimal.ZERO);
        dto.setIva(entidad.getIva() != null ? entidad.getIva() : BigDecimal.ZERO);

        // Blindaje de navegación segura para Pedido y Cliente
        if (entidad.getPedido() != null) {
            try {
                if (entidad.getPedido().getCliente() != null) {
                    dto.setNombreCliente(entidad.getPedido().getCliente().getNombre());
                    dto.setCorreoCliente(entidad.getPedido().getCliente().getCorreo());
                } else {
                    dto.setNombreCliente("Cliente no asignado");
                    dto.setCorreoCliente("N/A");
                }

                if (entidad.getPedido().getDetalles() != null) {
                    dto.setDetalles(entidad.getPedido().getDetalles().stream()
                            .map(MapeadorPedido::aDtoRespuestaDetalle)
                            .collect(Collectors.toList()));
                } else {
                    dto.setDetalles(new ArrayList<>());
                }
            } catch (Exception e) {
                // Si ocurre un error de Lazy loading, enviamos datos mínimos en lugar de Error 500
                dto.setNombreCliente("Error al cargar cliente");
                dto.setDetalles(new ArrayList<>());
            }
        } else {
            dto.setNombreCliente("Factura sin pedido");
            dto.setDetalles(new ArrayList<>());
        }

        return dto;
    }
}