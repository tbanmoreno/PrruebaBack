package com.valenci.mapper;

import com.valenci.dto.DtoRespuestaProducto;
import com.valenci.dto.DtoSolicitudProducto;
import com.valenci.entidades.Producto;
import java.util.List;
import java.util.stream.Collectors;

public class MapeadorProducto {

    public static Producto aEntidad(DtoSolicitudProducto dto) {
        if (dto == null) return null;
        Producto producto = new Producto();
        // Sincronización de nombres de campo
        producto.setNombreProducto(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        producto.setCantidad(dto.getCantidad());
        producto.setDescripcion(dto.getDescripcion());
        return producto;
    }


    public static DtoRespuestaProducto aDto(Producto entidad) {
        if (entidad == null) return null;

        String nombreProv = "Sin proveedor";

        // CORRECCIÓN: Verificación de persistencia para evitar LazyInitializationException
        if (entidad.getProveedor() != null) {
            try {
                // Intentamos acceder al nombre; si la sesión está cerrada, esto lanzará el error
                nombreProv = entidad.getProveedor().getNombreEmpresa();
            } catch (Exception e) {
                // Si falla, el DTO sigue vivo con "Sin proveedor" en lugar de dar Error 500
                nombreProv = "Proveedor no disponible";
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
        return entidades.stream().map(MapeadorProducto::aDto).collect(Collectors.toList());
    }
}
