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
@RequiredArgsConstructor // Sugerencia Senior: Reemplaza el constructor manual por esta anotación de Lombok
public class ControladorAutenticacion {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final ServicioUsuario servicioUsuario;

    // --- LOGIN ---
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

    // --- REGISTRO ---
    @PostMapping("/register")
    public ResponseEntity<DtoRespuestaAutenticacion> registrar(@RequestBody DtoRegistroUsuario request) {

        // 1. Forzamos la creación de un Cliente (Registro público siempre es Cliente)
        Cliente nuevoCliente = new Cliente();

        // 2. Llenamos los datos básicos
        nuevoCliente.setNombre(request.getNombre());
        nuevoCliente.setCorreo(request.getCorreo());
        nuevoCliente.setContrasena(request.getContrasena()); // El servicio la encriptará

        // 3. Mapeamos la dirección de envío (Campo que agregamos en el Register.jsx)
        nuevoCliente.setDireccionEnvio(request.getDireccionEnvio());

        // 4. Asignamos el Rol de forma segura
        nuevoCliente.setRol(Rol.CLIENTE);

        // 5. Guardamos usando el método de la interfaz
        servicioUsuario.registrar(nuevoCliente);

        // 6. Generamos token de acceso inmediato
        final String token = jwtService.generateToken(nuevoCliente);

        return ResponseEntity.ok(DtoRespuestaAutenticacion.builder().token(token).build());
    }
}