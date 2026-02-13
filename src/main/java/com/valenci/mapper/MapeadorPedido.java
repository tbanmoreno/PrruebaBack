package com.valenci.mapper;

import com.valenci.dto.*;
import com.valenci.entidades.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

public class MapeadorPedido {

    public static DtoRespuestaPedido aDtoRespuesta(Pedido pedido) {
        if (pedido == null) return null;

        DtoRespuestaPedido dto = new DtoRespuestaPedido();
        dto.setIdPedido(pedido.getIdPedido());

        // Blindaje de Fecha
        dto.setFechaPedido(pedido.getFechaPedido() != null ? pedido.getFechaPedido() : LocalDateTime.now());

        // Blindaje de Estado
        dto.setEstadoPedido(pedido.getEstadoPedido() != null ? pedido.getEstadoPedido().name() : "PENDIENTE");

        // Blindaje de Cliente
        if (pedido.getCliente() != null) {
            dto.setNombreCliente(pedido.getCliente().getNombre());
        } else {
            dto.setNombreCliente("Cliente no disponible");
        }

        // Blindaje de Total
        dto.setTotalPedido(pedido.getTotalPedido() != null ? pedido.getTotalPedido() : BigDecimal.ZERO);

        // Blindaje de Detalles
        if (pedido.getDetalles() != null) {
            dto.setDetalles(pedido.getDetalles().stream()
                    .map(MapeadorPedido::aDtoRespuestaDetalle)
                    .collect(Collectors.toList()));
        } else {
            dto.setDetalles(Collections.emptyList());
        }

        return dto;
    }

    public static DtoRespuestaDetallePedido aDtoRespuestaDetalle(DetallePedido detalle) {
        if (detalle == null) return null;

        DtoRespuestaDetallePedido dto = new DtoRespuestaDetallePedido();
        if (detalle.getProducto() != null) {
            dto.setNombreProducto(detalle.getProducto().getNombreProducto());
        } else {
            dto.setNombreProducto("Producto desconocido");
        }

        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario() != null ? detalle.getPrecioUnitario() : BigDecimal.ZERO);
        dto.setSubtotal(detalle.getSubtotal() != null ? detalle.getSubtotal() : BigDecimal.ZERO);
        return dto;
    }

    public static DtoPedidoHistorial aDtoHistorial(Pedido pedido, Factura factura) {
        if (pedido == null) return null;

        DtoPedidoHistorial dto = new DtoPedidoHistorial();
        dto.setIdPedido(pedido.getIdPedido());
        dto.setFechaPedido(pedido.getFechaPedido() != null ? pedido.getFechaPedido() : LocalDateTime.now());
        dto.setTotalPedido(pedido.getTotalPedido() != null ? pedido.getTotalPedido() : BigDecimal.ZERO);
        dto.setEstadoPedido(pedido.getEstadoPedido() != null ? pedido.getEstadoPedido().name() : "DESCONOCIDO");

        if (factura != null) {
            DtoResumenFactura res = new DtoResumenFactura(
                    factura.getIdFactura(),
                    factura.getFechaFactura() != null ? factura.getFechaFactura() : LocalDateTime.now(),
                    factura.getTotalFactura() != null ? factura.getTotalFactura() : BigDecimal.ZERO,
                    factura.getIva() != null ? factura.getIva() : BigDecimal.ZERO
            );

            if (pedido.getCliente() != null) {
                res.setNombreCliente(pedido.getCliente().getNombre());
                res.setCorreoCliente(pedido.getCliente().getCorreo());
            }

            res.setDetalles(pedido.getDetalles() != null ?
                    pedido.getDetalles().stream().map(MapeadorPedido::aDtoRespuestaDetalle).collect(Collectors.toList()) :
                    Collections.emptyList());

            dto.setFactura(res);
        }

        dto.setDetalles(pedido.getDetalles() != null ?
                pedido.getDetalles().stream().map(MapeadorPedido::aDtoRespuestaDetalle).collect(Collectors.toList()) :
                Collections.emptyList());

        return dto;
    }
}