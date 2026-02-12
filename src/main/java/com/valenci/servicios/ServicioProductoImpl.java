package com.valenci.servicios;

import com.valenci.dto.DtoRespuestaProducto;
import com.valenci.entidades.Producto;
import com.valenci.mapper.MapeadorProducto;
import com.valenci.repositorios.RepositorioProducto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServicioProductoImpl implements ServicioProducto {

    private final RepositorioProducto repositorioProducto;

    @Override
    @Transactional(readOnly = true)
    public List<DtoRespuestaProducto> listarTodosParaAdmin() {
        return repositorioProducto.findAllWithProveedor().stream()
                .map(MapeadorProducto::aDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Producto crear(Producto producto) {
        return repositorioProducto.save(producto);
    }

    @Override
    @Transactional
    public Producto actualizar(int idProducto, Producto datosNuevos) {
        Producto productoExistente = repositorioProducto.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        productoExistente.setNombreProducto(datosNuevos.getNombreProducto());
        productoExistente.setPrecio(datosNuevos.getPrecio());
        productoExistente.setCantidad(datosNuevos.getCantidad());
        productoExistente.setDescripcion(datosNuevos.getDescripcion());
        productoExistente.setProveedor(datosNuevos.getProveedor());

        return repositorioProducto.save(productoExistente);
    }

    @Override
    @Transactional
    public void eliminarPorId(int idProducto) {
        repositorioProducto.deleteById(idProducto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> buscarPorId(int idProducto) {
        return repositorioProducto.findByIdWithProveedor(idProducto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        return repositorioProducto.findAll();
    }
}