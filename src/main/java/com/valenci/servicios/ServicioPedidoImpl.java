package com.valenci.servicios;

import com.valenci.dto.DtoReporteVentas;
import com.valenci.entidades.*;
import com.valenci.repositorios.RepositorioPago;
import com.valenci.repositorios.RepositorioPedido;
import com.valenci.repositorios.RepositorioProducto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ServicioPedidoImpl implements ServicioPedido {

    private final RepositorioPedido repositorioPedido;
    private final RepositorioProducto repositorioProducto;
    private final RepositorioPago repositorioPago;
    private final ServicioFactura servicioFactura;

    public ServicioPedidoImpl(RepositorioPedido repositorioPedido,
                              RepositorioProducto repositorioProducto,
                              RepositorioPago repositorioPago,
                              ServicioFactura servicioFactura) {
        this.repositorioPedido = repositorioPedido;
        this.repositorioProducto = repositorioProducto;
        this.repositorioPago = repositorioPago;
        this.servicioFactura = servicioFactura;
    }

    @Override
    @Transactional
    public Pedido crear(Pedido pedido) {
        log.info("Creando pedido para cliente ID: {}", pedido.getCliente().getId());
        BigDecimal totalGeneral = BigDecimal.ZERO;

        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto productoEnDB = repositorioProducto.findById(detalle.getProducto().getIdProducto())
                    .orElseThrow(() -> new IllegalArgumentException("Producto ID " + detalle.getProducto().getIdProducto() + " no existe."));

            if (productoEnDB.getCantidad() < detalle.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente para: " + productoEnDB.getNombreProducto());
            }

            detalle.setPrecioUnitario(productoEnDB.getPrecio());
            BigDecimal subtotal = productoEnDB.getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad()));
            detalle.setSubtotal(subtotal);
            totalGeneral = totalGeneral.add(subtotal);

            productoEnDB.setCantidad(productoEnDB.getCantidad() - detalle.getCantidad());
            repositorioProducto.save(productoEnDB);

            detalle.setPedido(pedido);
        }

        pedido.setTotalPedido(totalGeneral);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstadoPedido(EstadoPedido.PENDIENTE);

        return repositorioPedido.save(pedido);
    }

    @Override
    @Transactional
    public void registrarPago(int idPedido, MetodoPago metodoPago) {
        Pedido pedido = buscarPorId(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado."));

        if (pedido.getEstadoPedido() != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden pagar pedidos en estado PENDIENTE.");
        }

        Pago nuevoPago = new Pago();
        nuevoPago.setPedido(pedido);
        nuevoPago.setMonto(pedido.getTotalPedido());
        nuevoPago.setFechaPago(LocalDateTime.now());
        nuevoPago.setMetodoPago(metodoPago);
        repositorioPago.save(nuevoPago);

        actualizarEstado(idPedido, EstadoPedido.PAGADO);
        servicioFactura.crearFacturaParaPedido(pedido);
    }

    @Override
    @Transactional
    public void actualizarEstado(int idPedido, EstadoPedido nuevoEstado) {
        Pedido pedido = buscarPorId(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado ID: " + idPedido));

        EstadoPedido estadoActual = pedido.getEstadoPedido();
        if (estadoActual == nuevoEstado) return;

        if (estadoActual == EstadoPedido.CANCELADO) {
            throw new IllegalStateException("No se puede reactivar un pedido CANCELADO.");
        }

        pedido.setEstadoPedido(nuevoEstado);
        repositorioPedido.save(pedido);
        log.info("Pedido ID {} cambió de {} a {}", idPedido, estadoActual, nuevoEstado);
    }

    @Override
    @Transactional
    public void cancelar(int idPedido) {
        Pedido pedido = repositorioPedido.findById(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado ID: " + idPedido));

        // GUARDIA LÓGICA: Solo cancelar si no está ya cancelado o entregado
        if (pedido.getEstadoPedido() == EstadoPedido.CANCELADO) {
            throw new IllegalStateException("El pedido ya se encuentra cancelado.");
        }

        if (pedido.getEstadoPedido() == EstadoPedido.ENTREGADO) {
            throw new IllegalStateException("No se puede cancelar un pedido que ya ha sido entregado al cliente.");
        }

        // 1. Cambiar el estado
        pedido.setEstadoPedido(EstadoPedido.CANCELADO);

        // 2. Devolver Stock de forma segura
        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto p = detalle.getProducto();
            p.setCantidad(p.getCantidad() + detalle.getCantidad());
            repositorioProducto.save(p);
        }

        repositorioPedido.save(pedido);
        log.info("Pedido ID {} cancelado exitosamente. Stock devuelto al inventario.", idPedido);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPorId(int idPedido) {
        return repositorioPedido.findById(idPedido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarPorCliente(int idCliente) {
        return repositorioPedido.findAllByClienteIdWithDetalles(idCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        return repositorioPedido.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarPorEstado(EstadoPedido estado) {
        return repositorioPedido.findByEstadoPedido(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarPorFecha(LocalDate fecha) {
        return repositorioPedido.findByFecha(fecha);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarPorProducto(int idProducto) {
        return repositorioPedido.findByProductoId(idProducto);
    }

    @Override
    @Transactional(readOnly = true)
    public DtoReporteVentas obtenerReporteGeneral() {
        return repositorioPedido.obtenerResumenVentas();
    }
}