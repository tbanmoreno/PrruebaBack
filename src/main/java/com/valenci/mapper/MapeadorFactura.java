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

        // Extraemos datos del cliente de forma segura (Safe-Navigation)
        if (entidad.getPedido() != null && entidad.getPedido().getCliente() != null) {
            dto.setNombreCliente(entidad.getPedido().getCliente().getNombre());
            dto.setCorreoCliente(entidad.getPedido().getCliente().getCorreo());

            // Mapeamos los detalles del pedido si existen
            if (entidad.getPedido().getDetalles() != null) {
                dto.setDetalles(entidad.getPedido().getDetalles().stream()
                        .map(MapeadorPedido::aDtoRespuestaDetalle)
                        .collect(Collectors.toList()));
            } else {
                dto.setDetalles(new ArrayList<>());
            }
        } else {
            dto.setNombreCliente("Cliente No Identificado");
            dto.setCorreoCliente("N/A");
            dto.setDetalles(new ArrayList<>());
        }

        return dto;
    }
}