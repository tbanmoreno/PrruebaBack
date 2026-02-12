package com.valenci.repositorios;

import com.valenci.entidades.Pedido;
import com.valenci.dto.DtoReporteVentas;
import com.valenci.entidades.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioPedido extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByEstadoPedido(EstadoPedido estado);

    @Query("SELECT p FROM Pedido p WHERE DATE(p.fechaPedido) = :fecha")
    List<Pedido> findByFecha(@Param("fecha") LocalDate fecha);

    @Query("SELECT p FROM Pedido p JOIN p.detalles d WHERE d.producto.id = :idProducto")
    List<Pedido> findByProductoId(@Param("idProducto") int idProducto);

    @Query("SELECT p FROM Pedido p JOIN FETCH p.cliente WHERE p.idPedido = :id")
    @Override
    Optional<Pedido> findById(@Param("id") Integer id);

    // Método optimizado para el historial
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.detalles d LEFT JOIN FETCH d.producto WHERE p.cliente.id = :idCliente")
    List<Pedido> findAllByClienteIdWithDetalles(@Param("idCliente") int idCliente);

    // --- REPORTE DE VENTAS ---
    @Query("""
        SELECT new com.valenci.dto.DtoReporteVentas(
            COALESCE(SUM(p.totalPedido), 0), 
            COUNT(p),
            'Ver Detalles' 
        )
        FROM Pedido p 
        WHERE p.estadoPedido IN (com.valenci.entidades.EstadoPedido.PAGADO, com.valenci.entidades.EstadoPedido.ENVIADO, com.valenci.entidades.EstadoPedido.ENTREGADO)
    """)
    DtoReporteVentas obtenerResumenVentas();
}