package com.valenci.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DtoRegistroUsuario {
    private String nombre;
    private String correo;
    private String contrasena;
    private String direccionEnvio;
    private String rol; // "CLIENTE", "PROVEEDOR", etc.
}