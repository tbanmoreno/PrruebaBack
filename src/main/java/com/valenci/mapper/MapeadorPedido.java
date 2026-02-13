package com.valenci.mapper;

import com.valenci.dto.*;
import com.valenci.entidades.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

        if (pedido.getCliente() != null) {
            dto.setNombreCliente(pedido.getCliente().getNombre());
        } else {
            dto.setNombreCliente("Usuario Valenci");
        }

        dto.setDetalles(pedido.getDetalles() != null ?
                pedido.getDetalles().stream().map(MapeadorPedido::aDtoRespuestaDetalle).collect(Collectors.toList()) :
                new ArrayList<>());

        return dto;
    }

    public static DtoRespuestaDetallePedido aDtoRespuestaDetalle(DetallePedido detalle) {
        if (detalle == null) return null;

        DtoRespuestaDetallePedido dto = new DtoRespuestaDetallePedido();
        if (detalle.getProducto() != null) {
            dto.setNombreProducto(detalle.getProducto().getNombreProducto());
        } else {
            dto.setNombreProducto("Producto No Identificado");
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
        dto.setEstadoPedido(pedido.getEstadoPedido() != null ? pedido.getEstadoPedido().name() : "PENDIENTE");

        if (factura != null) {
            // Usamos el constructor básico y luego inyectamos los campos de Frontend
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
                    new ArrayList<>());

            dto.setFactura(res);
        }

        dto.setDetalles(pedido.getDetalles() != null ?
                pedido.getDetalles().stream().map(MapeadorPedido::aDtoRespuestaDetalle).collect(Collectors.toList()) :
                new ArrayList<>());

        return dto;
    }
}