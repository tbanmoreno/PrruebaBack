package com.valenci.mapper;

import com.valenci.dto.DtoRespuestaProducto;
import com.valenci.dto.DtoSolicitudProducto;
import com.valenci.entidades.Producto;
import com.valenci.entidades.Proveedor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MapeadorProducto {

    public static Producto aEntidad(DtoSolicitudProducto dto) {
        if (dto == null) return null;
        Producto producto = new Producto();
        // Sincronizamos: 'nombre' del DTO -> 'nombreProducto' de la Entidad
        producto.setNombreProducto(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        producto.setCantidad(dto.getCantidad());
        producto.setDescripcion(dto.getDescripcion());
        // CRÍTICO: El mapper ahora se encarga de la imagen Base64
        producto.setImagen(dto.getImagen());
        return producto;
    }

    public static DtoRespuestaProducto aDto(Producto entidad) {
        if (entidad == null) return null;

        String nombreProv = "Sin proveedor";
        if (entidad.getProveedor() != null) {
            if (entidad.getProveedor() instanceof Proveedor) {
                nombreProv = ((Proveedor) entidad.getProveedor()).getNombreEmpresa();
            } else {
                nombreProv = entidad.getProveedor().getNombre();
            }
        }

        return new DtoRespuestaProducto(
                entidad.getIdProducto(), // El front recibe 'id'
                entidad.getNombreProducto(), // El front recibe 'nombre'
                entidad.getDescripcion(),
                entidad.getPrecio(),
                entidad.getCantidad(),
                nombreProv,
                entidad.getImagen()
        );
    }

    public static List<DtoRespuestaProducto> aListaDto(List<Producto> entidades) {
        if (entidades == null) return new ArrayList<>();
        return entidades.stream().map(MapeadorProducto::aDto).collect(Collectors.toList());
    }
}