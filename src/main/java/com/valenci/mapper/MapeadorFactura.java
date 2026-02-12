package com.valenci.mapper;

import com.valenci.dto.DtoRespuestaFactura;
import com.valenci.entidades.Factura;
import java.util.stream.Collectors;

public class MapeadorFactura {

    public static DtoRespuestaFactura aDto(Factura entidad) {
        if (entidad == null) return null;

        DtoRespuestaFactura dto = new DtoRespuestaFactura();
        dto.setIdFactura(entidad.getIdFactura());
        dto.setFecha(entidad.getFechaFactura());
        dto.setTotal(entidad.getTotalFactura());
        dto.setIva(entidad.getIva());

        // Extraemos datos del cliente a través del pedido vinculado a la factura
        if (entidad.getPedido() != null && entidad.getPedido().getCliente() != null) {
            dto.setNombreCliente(entidad.getPedido().getCliente().getNombre());
            dto.setCorreoCliente(entidad.getPedido().getCliente().getCorreo());

            // Mapeamos los detalles del pedido para la tabla de la factura
            dto.setDetalles(entidad.getPedido().getDetalles().stream()
                    .map(MapeadorPedido::aDtoRespuestaDetalle)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}