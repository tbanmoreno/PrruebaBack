package com.valenci.controladores;

import com.valenci.dto.DtoAdminReseteoContrasena;
import com.valenci.dto.DtoRespuestaUsuario;
import com.valenci.mapper.MapeadorUsuario;
import com.valenci.servicios.ServicioUsuario;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMINISTRADOR')")
@Slf4j
public class ControladorAdmin {

    private final ServicioUsuario servicioUsuario;

    public ControladorAdmin(ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<DtoRespuestaUsuario>> obtenerTodosLosUsuarios() {
        log.info("Acceso administrativo: Listando todos los usuarios del sistema.");
        List<DtoRespuestaUsuario> usuarios = MapeadorUsuario.aListaDtoRespuesta(servicioUsuario.listarTodos());
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Reset de contraseña blindado.
     * EXPLICACIÓN SENIOR: Ahora usamos un DTO tipado. Si el Frontend envía algo que no sea
     * un JSON válido con el campo 'nuevaContrasena', Spring rechazará la petición automáticamente.
     */
    @PatchMapping(value = "/usuarios/{id}/password-reset")
    public ResponseEntity<Void> resetearPassword(
            @PathVariable int id,
            @Valid @RequestBody DtoAdminReseteoContrasena solicitud) {

        log.warn("ADMIN ACTION: Solicitud de reset de clave para Usuario ID: {}", id);

        try {
            // Accedemos a la propiedad limpia del DTO
            servicioUsuario.adminResetearContrasena(id, solicitud.getNuevaContrasena());
            log.info("ÉXITO: Credencial actualizada para ID: {}", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("ERROR: No se pudo resetear la clave: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}