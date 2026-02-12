package com.valenci.servicios;

import com.valenci.dto.DtoReporteVentas;
import com.valenci.entidades.EstadoPedido;
import com.valenci.entidades.MetodoPago;
import com.valenci.entidades.Pedido;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ServicioPedido {

    Pedido crear(Pedido pedido);

    void registrarPago(int idPedido, MetodoPago metodoPago);

    Optional<Pedido> buscarPorId(int idPedido);

    List<Pedido> listarPorCliente(int idCliente);

    List<Pedido> listarTodos();

    List<Pedido> listarPorEstado(EstadoPedido estado);

    List<Pedido> listarPorFecha(LocalDate fecha);

    List<Pedido> listarPorProducto(int idProducto);

    void cancelar(int idPedido);

    void actualizarEstado(int idPedido, EstadoPedido nuevoEstado);

    DtoReporteVentas obtenerReporteGeneral();
}