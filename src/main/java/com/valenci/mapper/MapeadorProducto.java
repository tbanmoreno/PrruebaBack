package com.valenci.mapper;

import com.valenci.dto.DtoRespuestaProducto;
import com.valenci.dto.DtoSolicitudProducto;
import com.valenci.entidades.Producto;
import com.valenci.entidades.Proveedor;
import java.util.List;
import java.util.stream.Collectors;

public class MapeadorProducto {

    public static Producto aEntidad(DtoSolicitudProducto dto) {
        if (dto == null) return null;
        Producto producto = new Producto();
        producto.setNombreProducto(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        producto.setCantidad(dto.getCantidad());
        producto.setDescripcion(dto.getDescripcion());
        return producto;
    }

    public static DtoRespuestaProducto aDto(Producto entidad) {
        if (entidad == null) return null;

        String nombreProv = "Sin proveedor";

        // Verificamos si existe el proveedor y si es una instancia de la clase Proveedor
        if (entidad.getProveedor() != null) {
            if (entidad.getProveedor() instanceof Proveedor) {
                // Casting explícito para acceder al método getNombreEmpresa()
                nombreProv = ((Proveedor) entidad.getProveedor()).getNombreEmpresa();
            } else {
                // Si es un Usuario de otro tipo, usamos su nombre personal
                nombreProv = entidad.getProveedor().getNombre();
            }
        }

        return new DtoRespuestaProducto(
                entidad.getIdProducto(),
                entidad.getNombreProducto(),
                entidad.getDescripcion(),
                entidad.getPrecio(),
                entidad.getCantidad(),
                nombreProv
        );
    }

    public static List<DtoRespuestaProducto> aListaDto(List<Producto> entidades) {
        return entidades.stream()
                .map(MapeadorProducto::aDto)
                .collect(Collectors.toList());
    }
}