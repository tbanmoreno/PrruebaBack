package com.valenci.mapper;

import com.valenci.dto.*;
import com.valenci.entidades.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class MapeadorPedido {

    public static DtoRespuestaPedido aDtoRespuesta(Pedido pedido) {
        if (pedido == null) return null;

        DtoRespuestaPedido dto = new DtoRespuestaPedido();
        dto.setIdPedido(pedido.getIdPedido());
        dto.setFechaPedido(pedido.getFechaPedido() != null ? pedido.getFechaPedido() : LocalDateTime.now());
        dto.setEstadoPedido(pedido.getEstadoPedido() != null ? pedido.getEstadoPedido().name() : "PENDIENTE");
        dto.setTotalPedido(pedido.getTotalPedido() != null ? pedido.getTotalPedido() : BigDecimal.ZERO);

        // Verificación de Cliente con Fallback
        if (pedido.getCliente() != null) {
            dto.setNombreCliente(pedido.getCliente().getNombre());
        } else {
            dto.setNombreCliente("Usuario Valenci");
        }

        // Mapeo seguro de colecciones
        if (pedido.getDetalles() != null && !pedido.getDetalles().isEmpty()) {
            dto.setDetalles(pedido.getDetalles().stream()
                    .map(MapeadorPedido::aDtoRespuestaDetalle)
                    .collect(Collectors.toList()));
        } else {
            dto.setDetalles(new ArrayList<>());
        }

        return dto;
    }

    public static DtoRespuestaDetallePedido aDtoRespuestaDetalle(DetallePedido detalle) {
        if (detalle == null) return null;

        DtoRespuestaDetallePedido dto = new DtoRespuestaDetallePedido();

        // Verificación de Producto con Fallback
        if (detalle.getProducto() != null) {
            dto.setNombreProducto(detalle.getProducto().getNombreProducto());
        } else {
            dto.setNombreProducto("Variedad No Identificada");
        }

        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario() != null ? detalle.getPrecioUnitario() : BigDecimal.ZERO);
        dto.setSubtotal(detalle.getSubtotal() != null ? detalle.getSubtotal() : BigDecimal.ZERO);
        return dto;
    }
}