package com.valenci.controladores;

import com.valenci.dto.DtoCambioContrasena;
import com.valenci.dto.DtoPedidoHistorial;
import com.valenci.dto.DtoRespuestaUsuario;
import com.valenci.dto.DtoSolicitudActualizacionPerfil;
import com.valenci.entidades.Cliente;
import com.valenci.entidades.Usuario;
import com.valenci.mapper.MapeadorPedido;
import com.valenci.mapper.MapeadorUsuario;
import com.valenci.repositorios.RepositorioUsuario;
import com.valenci.servicios.ServicioFactura;
import com.valenci.servicios.ServicioPedido;
import com.valenci.servicios.ServicioUsuario;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cuenta")
@Slf4j
public class ControladorCuenta {

    private final ServicioPedido servicioPedido;
    private final ServicioUsuario servicioUsuario;
    private final RepositorioUsuario repositorioUsuario;
    private final ServicioFactura servicioFactura;

    public ControladorCuenta(ServicioPedido servicioPedido,
                             ServicioUsuario servicioUsuario,
                             RepositorioUsuario repositorioUsuario,
                             ServicioFactura servicioFactura) {
        this.servicioPedido = servicioPedido;
        this.servicioUsuario = servicioUsuario;
        this.repositorioUsuario = repositorioUsuario;
        this.servicioFactura = servicioFactura;
    }

    @GetMapping("/perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DtoRespuestaUsuario> verMiPerfil(@AuthenticationPrincipal Usuario usuarioAutenticado) {
        log.info("Solicitud de perfil para el usuario ID: {}", usuarioAutenticado.getId());
        return ResponseEntity.ok(MapeadorUsuario.aDtoRespuesta(usuarioAutenticado));
    }

    @PutMapping("/perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DtoRespuestaUsuario> actualizarMiPerfil(
            @AuthenticationPrincipal Usuario principal,
            @Valid @RequestBody DtoSolicitudActualizacionPerfil dto) {

        log.info("Iniciando actualización de perfil para usuario ID: {}", principal.getId());

        // 1. Buscamos la instancia fresca de la base de datos
        Usuario usuario = repositorioUsuario.findById(principal.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        try {
            // 2. Aplicamos cambios
            usuario.setNombre(dto.getNombre().trim());

            if (usuario instanceof Cliente cliente) {
                cliente.setDireccionEnvio(dto.getDireccionEnvio());
            }

            // 3. Guardar y Forzar escritura (flush)
            Usuario guardado = repositorioUsuario.saveAndFlush(usuario);
            log.info("Cambios persistidos en BD para: {}", guardado.getCorreo());

            // 4. Devolvemos el DTO con los nuevos datos
            return ResponseEntity.ok(MapeadorUsuario.aDtoRespuesta(guardado));

        } catch (Exception e) {
            log.error("Fallo crítico al guardar perfil", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno al procesar los datos");
        }
    }

    @PutMapping("/cambiar-contrasena")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> cambiarMiContrasena(
            @AuthenticationPrincipal Usuario usuarioAutenticado,
            @Valid @RequestBody DtoCambioContrasena dto) {
        try {
            servicioUsuario.cambiarContrasena(usuarioAutenticado, dto.getContrasenaActual(), dto.getNuevaContrasena());
            return ResponseEntity.ok().build();
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/historial")
    @PreAuthorize("hasAuthority('CLIENTE')")
    public ResponseEntity<List<DtoPedidoHistorial>> verMiHistorial(@AuthenticationPrincipal Usuario usuarioAutenticado) {
        log.info("Solicitud de historial para el cliente ID: {}", usuarioAutenticado.getId());

        List<DtoPedidoHistorial> historial = servicioPedido.listarPorCliente(usuarioAutenticado.getId()).stream()
                .map(pedido -> {
                    var factura = servicioFactura.buscarPorIdPedido(pedido.getIdPedido()).orElse(null);
                    return MapeadorPedido.aDtoHistorial(pedido, factura);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(historial);
    }
}