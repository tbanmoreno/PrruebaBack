package com.valenci.mapper;

import com.valenci.dto.DtoSolicitudPago;
import com.valenci.dto.DtoRespuestaPago; // Asegúrate de tener este DTO creado
import com.valenci.entidades.Pago;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MapeadorPago {

    public static DtoRespuestaPago aDto(Pago entidad) {
        if (entidad == null) return null;

        DtoRespuestaPago dto = new DtoRespuestaPago();
        dto.setIdPago(entidad.getIdPago());

        // Blindaje contra nulos en montos y fechas
        dto.setMonto(entidad.getMonto() != null ? entidad.getMonto() : BigDecimal.ZERO);
        dto.setFechaPago(entidad.getFechaPago() != null ? entidad.getFechaPago() : LocalDateTime.now());

        // Manejo seguro del Enum de Método de Pago
        dto.setMetodoPago(entidad.getMetodoPago() != null ? entidad.getMetodoPago().name() : "NO_DEFINIDO");

        // Referencia segura al Pedido para evitar LazyInitializationException
        if (entidad.getPedido() != null) {
            dto.setIdPedido(entidad.getPedido().getIdPedido());
            // Si el cliente existe en el pedido, lo enviamos para el historial
            if (entidad.getPedido().getCliente() != null) {
                dto.setNombreCliente(entidad.getPedido().getCliente().getNombre());
            }
        }

        return dto;
    }

    public static List<DtoRespuestaPago> aListaDto(List<Pago> entidades) {
        if (entidades == null) return new ArrayList<>();
        return entidades.stream()
                .map(MapeadorPago::aDto)
                .collect(Collectors.toList());
    }
}