package com.valenci.controladores;

import com.valenci.dto.DtoRegistroUsuario;
import com.valenci.dto.DtoSolicitudAutenticacion;
import com.valenci.dto.DtoRespuestaAutenticacion;
import com.valenci.entidades.Cliente;
import com.valenci.entidades.Rol;
import com.valenci.entidades.Usuario;
import com.valenci.servicios.JwtService;
import com.valenci.servicios.ServicioUsuario;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class ControladorAutenticacion {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final ServicioUsuario servicioUsuario;

    public ControladorAutenticacion(JwtService jwtService,
                                    AuthenticationManager authenticationManager,
                                    UserDetailsService userDetailsService,
                                    ServicioUsuario servicioUsuario) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.servicioUsuario = servicioUsuario;
    }

    // --- LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<DtoRespuestaAutenticacion> iniciarSesion(@RequestBody DtoSolicitudAutenticacion authRequest) {
        // Esto valida correo y contraseña contra la BD
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getCorreo(),
                        authRequest.getContrasena()
                )
        );

        // Si la autenticación falla, Spring Security lanza una excepción antes de llegar aquí.
        // Si llegamos aquí, cargamos el usuario y generamos el token.
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getCorreo());
        final String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(DtoRespuestaAutenticacion.builder().token(token).build());
    }

    // --- REGISTRO ---
    @PostMapping("/register")
    public ResponseEntity<DtoRespuestaAutenticacion> registrar(@RequestBody DtoRegistroUsuario request) {

        Usuario nuevoUsuario;

        // 1. Instanciamos según el rol (Por ahora nos enfocamos en Cliente)
        if ("CLIENTE".equalsIgnoreCase(request.getRol())) {
            Cliente cliente = new Cliente();
            // Aquí podrías agregar campos extra de cliente si el DTO los tuviera
            nuevoUsuario = cliente;
        } else {
            nuevoUsuario = new Cliente();
        }

        // 2. Llenamos los datos básicos
        nuevoUsuario.setNombre(request.getNombre());
        nuevoUsuario.setCorreo(request.getCorreo());

        // OJO: Pasamos la contraseña PLANA. El servicio se encargará de encriptarla.
        nuevoUsuario.setContrasena(request.getContrasena());

        // 3. Asignamos el Rol
        try {
            nuevoUsuario.setRol(Rol.valueOf(request.getRol().toUpperCase()));
        } catch (Exception e) {
            nuevoUsuario.setRol(Rol.CLIENTE); // Rol por defecto
        }

        // 4. Guardamos usando el método correcto de la interfaz: REGISTRAR
        servicioUsuario.registrar(nuevoUsuario);

        // 5. Generamos token y devolvemos
        final String token = jwtService.generateToken(nuevoUsuario);

        return ResponseEntity.ok(DtoRespuestaAutenticacion.builder().token(token).build());
    }
}