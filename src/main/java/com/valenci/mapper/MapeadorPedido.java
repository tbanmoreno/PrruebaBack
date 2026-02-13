package com.valenci.mapper;

import com.valenci.dto.DtoResumenFactura;
import com.valenci.entidades.DetallePedido;
import com.valenci.entidades.Factura;
import com.valenci.entidades.Pedido;
import com.valenci.dto.DtoRespuestaDetallePedido;
import com.valenci.dto.DtoPedidoHistorial;
import com.valenci.dto.DtoRespuestaPedido;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.stream.Collectors;

public class MapeadorPedido {

    public static DtoRespuestaPedido aDtoRespuesta(Pedido pedido) {
        if (pedido == null) return null;
        DtoRespuestaPedido dto = new DtoRespuestaPedido();
        dto.setIdPedido(pedido.getIdPedido());
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setEstadoPedido(pedido.getEstadoPedido() != null ? pedido.getEstadoPedido().name() : "PENDIENTE");

        // Verificación segura del cliente
        if (pedido.getCliente() != null) {
            dto.setNombreCliente(pedido.getCliente().getNombre());
        } else {
            dto.setNombreCliente("Cliente no disponible");
        }

        dto.setTotalPedido(pedido.getTotalPedido() != null ? pedido.getTotalPedido() : java.math.BigDecimal.ZERO);

        // Verificación segura de detalles
        dto.setDetalles(pedido.getDetalles() != null ?
                pedido.getDetalles().stream().map(MapeadorPedido::aDtoRespuestaDetalle).collect(Collectors.toList()) :
                Collections.emptyList());

        return dto;
    }

    public static DtoRespuestaDetallePedido aDtoRespuestaDetalle(DetallePedido detalle) {
        if (detalle == null) return null;
        DtoRespuestaDetallePedido dto = new DtoRespuestaDetallePedido();
        if (detalle.getProducto() != null) {
            dto.setNombreProducto(detalle.getProducto().getNombreProducto());
        }
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setSubtotal(detalle.getSubtotal());
        return dto;
    }

    public static DtoPedidoHistorial aDtoHistorial(Pedido pedido, Factura factura) {
        if (pedido == null) return null;
        DtoPedidoHistorial dto = new DtoPedidoHistorial();
        dto.setIdPedido(pedido.getIdPedido());
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setTotalPedido(pedido.getTotalPedido());
        dto.setEstadoPedido(pedido.getEstadoPedido().name());

        if (factura != null) {
            BigDecimal ivaSeguro = factura.getIva() != null ? factura.getIva() : BigDecimal.ZERO;

            // CREAMOS EL RESUMEN
            DtoResumenFactura res = new DtoResumenFactura(
                    factura.getIdFactura(),
                    factura.getFechaFactura(),
                    factura.getTotalFactura(),
                    ivaSeguro
            );

            // INYECTAMOS LA DATA PARA EL FRONTEND
            res.setNombreCliente(pedido.getCliente().getNombre());
            res.setCorreoCliente(pedido.getCliente().getCorreo());

            // MAPEAMOS LOS DETALLES DENTRO DE LA FACTURA
            res.setDetalles(pedido.getDetalles().stream()
                    .map(MapeadorPedido::aDtoRespuestaDetalle)
                    .collect(Collectors.toList()));

            dto.setFactura(res);
        }

        if (pedido.getDetalles() != null) {
            dto.setDetalles(pedido.getDetalles().stream()
                    .map(MapeadorPedido::aDtoRespuestaDetalle)
                    .collect(Collectors.toList()));
        } else {
            dto.setDetalles(Collections.emptyList());
        }
        return dto;
    }
}