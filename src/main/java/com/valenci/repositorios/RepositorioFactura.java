package com.valenci.repositorios;

import com.valenci.entidades.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioFactura extends JpaRepository<Factura, Integer> {

    @Query("SELECT f FROM Factura f JOIN FETCH f.pedido p JOIN FETCH p.cliente WHERE f.idFactura = :id")
    @Override
    Optional<Factura> findById(@Param("id") Integer id);

    @Query("SELECT DISTINCT f FROM Factura f JOIN FETCH f.pedido p JOIN FETCH p.cliente LEFT JOIN FETCH p.detalles d LEFT JOIN FETCH d.producto")
    @Override
    List<Factura> findAll();

    @Query("SELECT f FROM Factura f JOIN FETCH f.pedido p JOIN FETCH p.cliente WHERE p.idPedido = :idPedido")
    Optional<Factura> findByPedidoIdPedido(@Param("idPedido") int idPedido);

    @Query("SELECT f FROM Factura f JOIN FETCH f.pedido p JOIN FETCH p.cliente WHERE p.cliente.id = :idCliente")
    List<Factura> findByPedidoClienteId(@Param("idCliente") int idCliente);
}