package com.valenci.repositorios;

import com.valenci.entidades.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioProducto extends JpaRepository<Producto, Integer> {

    // JOIN FETCH garantiza que el Proveedor esté disponible para el Mapeador
    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.proveedor")
    List<Producto> findAllWithProveedor();

    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.proveedor WHERE p.idProducto = :id")
    Optional<Producto> findByIdWithProveedor(@Param("id") Integer id);
}