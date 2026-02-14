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
    public ResponseEntity<DtoRespuestaUsuario> verMiPerfil(@AuthenticationPrincipal Usuario usuarioAutenticado) {
        return ResponseEntity.ok(MapeadorUsuario.aDtoRespuesta(usuarioAutenticado));
    }

    @PutMapping("/perfil")
    public ResponseEntity<DtoRespuestaUsuario> actualizarMiPerfil(
            @AuthenticationPrincipal Usuario principal,
            @Valid @RequestBody DtoSolicitudActualizacionPerfil dto) {

        Usuario usuario = repositorioUsuario.findById(principal.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        try {
            usuario.setNombre(dto.getNombre().trim());
            if (usuario instanceof Cliente cliente) {
                cliente.setDireccionEnvio(dto.getDireccionEnvio());
            }

            Usuario guardado = repositorioUsuario.saveAndFlush(usuario);
            log.info("Perfil actualizado para: {}", guardado.getCorreo());

            return ResponseEntity.ok(MapeadorUsuario.aDtoRespuesta(guardado));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar perfil");
        }
    }

    @PutMapping("/cambiar-contrasena")
    public ResponseEntity<Void> cambiarMiContrasena(
            @AuthenticationPrincipal Usuario usuarioAutenticado,
            @Valid @RequestBody DtoCambioContrasena dto) {
        try {
            servicioUsuario.cambiarContrasena(usuarioAutenticado, dto.getContrasenaActual(), dto.getNuevaContrasena());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/historial")
    @PreAuthorize("hasAuthority('CLIENTE')")
    public ResponseEntity<List<DtoPedidoHistorial>> verMiHistorial(@AuthenticationPrincipal Usuario usuarioAutenticado) {
        List<DtoPedidoHistorial> historial = servicioPedido.listarPorCliente(usuarioAutenticado.getId()).stream()
                .map(pedido -> {
                    var factura = servicioFactura.buscarPorIdPedido(pedido.getIdPedido()).orElse(null);
                    return MapeadorPedido.aDtoHistorial(pedido, factura);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(historial);
    }
}