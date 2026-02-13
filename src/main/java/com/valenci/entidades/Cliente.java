package com.valenci.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@DiscriminatorValue("CLIENTE")
@Getter
@Setter
@NoArgsConstructor
// Blindaje contra proxies de Hibernate para evitar errores 500 en Render
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Cliente extends Usuario {

    @Column(name = "direccion_envio")
    private String direccionEnvio;

    // Relación inversa para el historial de compras
    // @JsonIgnore es vital aquí: corta la recursión si se consulta el cliente directamente
    @OneToMany(mappedBy = "cliente")
    @JsonIgnore
    private List<Pedido> pedidos;

    public Cliente(String nombre, String correo, String contrasena, String direccionEnvio) {
        super(nombre, correo, contrasena);
        this.direccionEnvio = direccionEnvio;
    }
}