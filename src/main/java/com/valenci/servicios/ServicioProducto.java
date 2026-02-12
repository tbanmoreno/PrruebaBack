package com.valenci.servicios;

import com.valenci.dto.DtoRespuestaProducto; // <-- Añadido
import com.valenci.entidades.Producto;
import java.util.List;
import java.util.Optional;

public interface ServicioProducto {
    Producto crear(Producto producto);
    Producto actualizar(int idProducto, Producto productoConDatosNuevos);
    void eliminarPorId(int idProducto);
    Optional<Producto> buscarPorId(int idProducto);
    List<Producto> listarTodos();

    // MÉTODO NUEVO PARA ELIMINAR EL ERROR 500 EN ADMIN
    List<DtoRespuestaProducto> listarTodosParaAdmin();
}