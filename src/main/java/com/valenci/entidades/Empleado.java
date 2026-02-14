package com.valenci.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("EMPLEADO")
@Getter
@Setter
@NoArgsConstructor
public class Empleado extends Usuario {

    @Column(name = "cargo")
    private String cargo;

    @Column(name = "salario")
    private BigDecimal salario;

    public Empleado(String nombre, String correo, String contrasena, String cargo, BigDecimal salario) {
        super(nombre, correo, contrasena);
        this.cargo = cargo;
        this.salario = salario;
    }
}