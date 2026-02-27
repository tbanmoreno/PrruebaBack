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

        // .trim() para limpiar espacios como "PAGADO " que bloquean el Frontend
        dto.setEstadoPedido(pedido.getEstadoPedido() != null ? pedido.getEstadoPedido().name().trim() : "PENDIENTE");

        dto.setTotalPedido(pedido.getTotalPedido() != null ? pedido.getTotalPedido() : BigDecimal.ZERO);
        dto.setNombreCliente(pedido.getCliente() != null ? pedido.getCliente().getNombre() : "Usuario Valenci");

        dto.setDetalles(pedido.getDetalles() != null ?
                pedido.getDetalles().stream()
                        .filter(d -> d != null)
                        .map(MapeadorPedido::aDtoRespuestaDetalle)
                        .collect(Collectors.toList()) :
                new ArrayList<>());
        return dto;
    }

    public static DtoRespuestaDetallePedido aDtoRespuestaDetalle(DetallePedido detalle) {
        if (detalle == null) return null;
        DtoRespuestaDetallePedido dto = new DtoRespuestaDetallePedido();
        if (detalle.getProducto() != null) {
            dto.setNombreProducto(detalle.getProducto().getNombreProducto());
        } else {
            dto.setNombreProducto("Producto Valenci");
        }
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario() != null ? detalle.getPrecioUnitario() : BigDecimal.ZERO);
        dto.setSubtotal(detalle.getSubtotal() != null ? detalle.getSubtotal() : BigDecimal.ZERO);
        return dto;
    }

    public static DtoRespuestaFactura aDtoFactura(Factura factura) {
        if (factura == null) return null;

        DtoRespuestaFactura dto = new DtoRespuestaFactura();
        dto.setIdFactura(factura.getIdFactura());
        dto.setFecha(factura.getFechaFactura() != null ? factura.getFechaFactura() : LocalDateTime.now());
        dto.setTotal(factura.getTotalFactura() != null ? factura.getTotalFactura() : BigDecimal.ZERO);
        dto.setIva(factura.getIva() != null ? factura.getIva() : BigDecimal.ZERO);
        dto.setDetalles(new ArrayList<>());

        if (factura.getPedido() != null) {
            Pedido p = factura.getPedido();
            dto.setNombreCliente(p.getCliente() != null ? p.getCliente().getNombre() : "Cliente Valenci");
            dto.setCorreoCliente(p.getCliente() != null ? p.getCliente().getCorreo() : "contacto@valenci.com");

            if (p.getDetalles() != null) {
                dto.setDetalles(p.getDetalles().stream()
                        .filter(d -> d != null)
                        .map(MapeadorPedido::aDtoRespuestaDetalle)
                        .collect(Collectors.toList()));
            }
        }
        return dto;
    }

    public static DtoPedidoHistorial aDtoHistorial(Pedido pedido, Factura factura) {
        if (pedido == null) return null;

        DtoPedidoHistorial dto = new DtoPedidoHistorial();
        dto.setIdPedido(pedido.getIdPedido());
        dto.setFechaPedido(pedido.getFechaPedido() != null ? pedido.getFechaPedido() : LocalDateTime.now());
        dto.setTotalPedido(pedido.getTotalPedido() != null ? pedido.getTotalPedido() : BigDecimal.ZERO);
        // Limpieza de espacios para el frontend
        dto.setEstadoPedido(pedido.getEstadoPedido() != null ? pedido.getEstadoPedido().name().trim() : "PENDIENTE");

        // Detalles del pedido (siempre presentes)
        dto.setDetalles(pedido.getDetalles() != null ?
                pedido.getDetalles().stream()
                        .filter(d -> d != null)
                        .map(MapeadorPedido::aDtoRespuestaDetalle)
                        .collect(Collectors.toList()) :
                new ArrayList<>());

        // Datos de Factura (solo si existe)
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

            // La factura también lleva sus detalles
            res.setDetalles(dto.getDetalles());
            dto.setFactura(res);
        }

        return dto;
    }
}