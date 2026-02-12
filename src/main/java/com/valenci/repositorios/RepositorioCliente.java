package com.valenci.repositorios;

import com.valenci.entidades.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioCliente extends JpaRepository<Cliente, Integer> {

    @Query("SELECT c FROM Cliente c")
    List<Cliente> findAllClientes();

    // Ahora que 'pedidos' existe en la entidad Cliente, esta consulta funcionará:
    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.pedidos WHERE c.id = :id")
    Optional<Cliente> findByIdWithPedidos(@Param("id") int id);
}