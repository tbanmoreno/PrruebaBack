package com.valenci.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Cliente extends Usuario {

    @Column(name = "direccion_envio")
    private String direccionEnvio;

    // SOLUCIÓN AL ERROR: Añadimos la relación inversa para el historial
    // Usamos JsonIgnore para evitar que al cargar un cliente se carguen infinitamente sus pedidos en JSON simple
    @OneToMany(mappedBy = "cliente")
    @JsonIgnore
    private List<Pedido> pedidos;

    public Cliente(String nombre, String correo, String contrasena, String direccionEnvio) {
        super(nombre, correo, contrasena);
        this.direccionEnvio = direccionEnvio;
    }
}