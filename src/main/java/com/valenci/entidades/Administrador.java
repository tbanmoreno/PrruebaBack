package com.valenci.entidades;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("ADMINISTRADOR")
@Getter
@Setter
@NoArgsConstructor
public class Administrador extends Usuario {
    // Constructor limpio: el ID y el Rol son gestionados por JPA automáticamente
    public Administrador(String nombre, String correo, String contrasena) {
        super(nombre, correo, contrasena);
    }
}