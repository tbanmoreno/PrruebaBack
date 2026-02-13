package com.valenci.controladores;

import com.valenci.dto.DtoRespuestaUsuario;
import com.valenci.mapper.MapeadorUsuario;
import com.valenci.servicios.ServicioUsuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
// CORRECCIÓN: hasAuthority('ADMINISTRADOR') coincide con el JWT generado por JwtService
@PreAuthorize("hasAuthority('ADMINISTRADOR')")
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class ControladorAdmin {

    private final ServicioUsuario servicioUsuario;

    public ControladorAdmin(ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
    }

    /**
     * Lista todos los usuarios (Administradores, Clientes y Proveedores)
     */
    @GetMapping("/usuarios")
    public ResponseEntity<List<DtoRespuestaUsuario>> obtenerTodosLosUsuarios() {
        log.info("Acceso administrativo: Listando todos los usuarios del sistema.");

        List<DtoRespuestaUsuario> usuarios = MapeadorUsuario.aListaDtoRespuesta(servicioUsuario.listarTodos());

        log.info("Operación exitosa. {} usuarios recuperados.", usuarios.size());
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Reset de contraseña forzado por el administrador.
     * Recibe la nueva contraseña y la procesa a través del servicio de seguridad.
     */
    @PatchMapping(value = "/usuarios/{id}/password-reset", consumes = "application/json")
    public ResponseEntity<Void> resetearPassword(@PathVariable int id, @RequestBody String nuevaPassword) {
        log.warn("INICIO DE RESET DE CLAVE: Administrador modificando acceso para Usuario ID: {}", id);

        // Limpieza de caracteres residuales de JSON si se envía como texto plano
        String passwordLimpia = nuevaPassword.replace("\"", "").trim();

        if (passwordLimpia.isEmpty()) {
            log.error("Fallo en reset: La nueva contraseña no puede estar vacía.");
            return ResponseEntity.badRequest().build();
        }

        try {
            // El servicio se encarga de aplicar BCrypt antes de persistir en DB
            servicioUsuario.adminResetearContrasena(id, passwordLimpia);

            log.info("ACCESO RESTABLECIDO: Nueva credencial encriptada y guardada para ID: {}", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("ERROR CRÍTICO: No se pudo resetear la clave del ID {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}