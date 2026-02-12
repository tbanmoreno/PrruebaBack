package com.valenci.controladores;

import com.valenci.dto.DtoRespuestaUsuario;
import com.valenci.mapper.MapeadorUsuario;
import com.valenci.servicios.ServicioUsuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
// CORRECCIÓN: Usamos hasAuthority para coincidir con el token JWT sin prefijo ROLE_
@PreAuthorize("hasAuthority('ADMINISTRADOR')")
@Slf4j
public class ControladorAdmin {

    private final ServicioUsuario servicioUsuario;

    public ControladorAdmin(ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
    }

    /**
     * Endpoint para que un administrador obtenga una lista de todos los usuarios del sistema.
     */
    @GetMapping("/usuarios")
    public ResponseEntity<List<DtoRespuestaUsuario>> obtenerTodosLosUsuarios() {
        log.info("Solicitud de administrador para listar todos los usuarios.");

        List<DtoRespuestaUsuario> usuarios = MapeadorUsuario.aListaDtoRespuesta(servicioUsuario.listarTodos());

        log.info("Se devolvieron {} usuarios.", usuarios.size());
        return ResponseEntity.ok(usuarios);
    }
}