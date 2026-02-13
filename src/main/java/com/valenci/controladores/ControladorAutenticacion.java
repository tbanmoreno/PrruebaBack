package com.valenci.controladores;

import com.valenci.dto.DtoRegistroUsuario;
import com.valenci.dto.DtoSolicitudAutenticacion;
import com.valenci.dto.DtoRespuestaAutenticacion;
import com.valenci.entidades.Cliente;
import com.valenci.entidades.Rol;
import com.valenci.entidades.Usuario;
import com.valenci.servicios.JwtService;
import com.valenci.servicios.ServicioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ControladorAutenticacion {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final ServicioUsuario servicioUsuario;

    @PostMapping("/login")
    public ResponseEntity<DtoRespuestaAutenticacion> iniciarSesion(@RequestBody DtoSolicitudAutenticacion authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getCorreo(),
                        authRequest.getContrasena()
                )
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getCorreo());
        final String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(DtoRespuestaAutenticacion.builder().token(token).build());
    }

    @PostMapping("/register")
    public ResponseEntity<DtoRespuestaAutenticacion> registrar(@RequestBody DtoRegistroUsuario request) {

        Cliente nuevoCliente = new Cliente();

        nuevoCliente.setNombre(request.getNombre());
        nuevoCliente.setCorreo(request.getCorreo());
        nuevoCliente.setContrasena(request.getContrasena());
        nuevoCliente.setDireccionEnvio(request.getDireccionEnvio());

        // CORRECCIÓN: Convertimos el Enum Rol a String para que coincida con la entidad Usuario
        nuevoCliente.setRol(Rol.CLIENTE.name());

        servicioUsuario.registrar(nuevoCliente);

        final String token = jwtService.generateToken(nuevoCliente);

        return ResponseEntity.ok(DtoRespuestaAutenticacion.builder().token(token).build());
    }
}