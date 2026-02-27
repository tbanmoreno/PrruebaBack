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
        // Sincronización de nombres: dto.nombre -> entidad.nombreProducto
        producto.setNombreProducto(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        producto.setCantidad(dto.getCantidad());
        producto.setDescripcion(dto.getDescripcion());
        producto.setImagen(dto.getImagen());
        return producto;
    }

    public static DtoRespuestaProducto aDto(Producto entidad) {
        if (entidad == null) return null;

        String nombreProv = "Sin proveedor";

        // Blindaje contra NullPointerException y ClassCastException
        if (entidad.getProveedor() != null) {
            try {
                if (entidad.getProveedor() instanceof Proveedor) {
                    nombreProv = ((Proveedor) entidad.getProveedor()).getNombreEmpresa();
                } else {
                    // Si el usuario existe pero no es entidad Proveedor, usamos su nombre base
                    nombreProv = entidad.getProveedor().getNombre() != null
                            ? entidad.getProveedor().getNombre()
                            : "Usuario sin nombre";
                }
            } catch (Exception e) {
                nombreProv = "Error en datos de proveedor";
            }
        }

        return new DtoRespuestaProducto(
                entidad.getIdProducto(),
                entidad.getNombreProducto() != null ? entidad.getNombreProducto() : "Producto sin nombre",
                entidad.getDescripcion(),
                entidad.getPrecio(),
                entidad.getCantidad(),
                nombreProv,
                entidad.getImagen()
        );
    }

    public static List<DtoRespuestaProducto> aListaDto(List<Producto> entidades) {
        if (entidades == null) return new ArrayList<>();
        return entidades.stream()
                .map(MapeadorProducto::aDto)
                .collect(Collectors.toList());
    }
}