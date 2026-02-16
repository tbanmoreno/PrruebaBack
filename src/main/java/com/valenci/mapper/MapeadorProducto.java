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
        producto.setNombreProducto(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        producto.setCantidad(dto.getCantidad());
        producto.setDescripcion(dto.getDescripcion());
        // La imagen se setea manualmente en el controlador tras la conversión si fuera necesario,
        // o directamente si el DTO de solicitud ya la trae en Base64.
        return producto;
    }

    public static DtoRespuestaProducto aDto(Producto entidad) {
        if (entidad == null) return null;

        String nombreProv = "Sin proveedor";

        if (entidad.getProveedor() != null) {
            try {
                if (entidad.getProveedor() instanceof Proveedor) {
                    nombreProv = ((Proveedor) entidad.getProveedor()).getNombreEmpresa();
                } else {
                    nombreProv = entidad.getProveedor().getNombre();
                }
            } catch (Exception e) {
                nombreProv = "Error de carga";
            }
        }

        // Retornamos el DTO incluyendo el nuevo campo 'imagen'
        return new DtoRespuestaProducto(
                entidad.getIdProducto(),
                entidad.getNombreProducto(),
                entidad.getDescripcion(),
                entidad.getPrecio(),
                entidad.getCantidad(),
                nombreProv,
                entidad.getImagen() // <-- CRUCIAL: Pasamos el String Base64 al Frontend
        );
    }

    public static List<DtoRespuestaProducto> aListaDto(List<Producto> entidades) {
        if (entidades == null) return new ArrayList<>();
        return entidades.stream()
                .map(MapeadorProducto::aDto)
                .collect(Collectors.toList());
    }
}