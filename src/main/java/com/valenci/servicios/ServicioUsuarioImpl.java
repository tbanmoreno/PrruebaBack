package com.valenci.servicios;

import com.valenci.entidades.Usuario;
import com.valenci.repositorios.RepositorioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServicioUsuarioImpl implements ServicioUsuario {

    private final RepositorioUsuario repositorioUsuario;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void registrar(Usuario usuario) {
        if (repositorioUsuario.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new IllegalArgumentException("El correo electrónico ya se encuentra registrado en el sistema.");
        }
        // Encriptación obligatoria antes de persistir
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        repositorioUsuario.save(usuario);
    }

    @Override
    @Transactional
    public void actualizar(Usuario usuario) {
        if (!repositorioUsuario.existsById(usuario.getId())) {
            throw new IllegalArgumentException("No se puede actualizar: Usuario no hallado.");
        }
        repositorioUsuario.save(usuario);
    }

    @Override public Optional<Usuario> buscarPorId(int id) { return repositorioUsuario.findById(id); }
    @Override public Optional<Usuario> buscarPorCorreo(String correo) { return repositorioUsuario.findByCorreo(correo); }
    @Override public List<Usuario> listarTodos() { return repositorioUsuario.findAll(); }
    @Override @Transactional public void eliminarPorId(int id) { repositorioUsuario.deleteById(id); }

    @Override
    @Transactional
    public void cambiarContrasena(Usuario usuario, String contrasenaActual, String nuevaContrasena) {
        if (!passwordEncoder.matches(contrasenaActual, usuario.getContrasena())) {
            throw new IllegalArgumentException("La credencial actual es incorrecta.");
        }
        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        repositorioUsuario.save(usuario);
    }

    @Override
    @Transactional
    public void adminResetearContrasena(int idUsuario, String nuevaContrasena) {
        Usuario usuario = repositorioUsuario.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no hallado para reset."));
        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        repositorioUsuario.save(usuario);
    }
}