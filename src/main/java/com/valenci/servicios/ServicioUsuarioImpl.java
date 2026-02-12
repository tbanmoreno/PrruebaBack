package com.valenci.servicios;

import com.valenci.entidades.Usuario;
import com.valenci.repositorios.RepositorioUsuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioUsuarioImpl implements ServicioUsuario {

    private final RepositorioUsuario repositorioUsuario;
    private final PasswordEncoder passwordEncoder;

    public ServicioUsuarioImpl(RepositorioUsuario repositorioUsuario, PasswordEncoder passwordEncoder) {
        this.repositorioUsuario = repositorioUsuario;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void registrar(Usuario usuario) {
        // Validar si ya existe el correo
        if (repositorioUsuario.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado.");
        }

        // Encriptar la contraseña antes de guardar
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));

        repositorioUsuario.save(usuario);
    }

    @Override
    @Transactional
    public void actualizar(Usuario usuario) {
        // Verificamos que exista
        if (!repositorioUsuario.existsById(usuario.getId())) {
            throw new IllegalArgumentException("Usuario no encontrado.");
        }
        // Nota: Aquí deberías tener cuidado de no sobrescribir la contraseña con una sin hashear
        // si el objeto usuario viene del frontend sin contraseña o con una nueva.
        repositorioUsuario.save(usuario);
    }

    @Override
    public Optional<Usuario> buscarPorId(int id) {
        return repositorioUsuario.findById(id);
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return repositorioUsuario.findByCorreo(correo);
    }

    @Override
    public List<Usuario> listarTodos() {
        return repositorioUsuario.findAll();
    }

    @Override
    @Transactional
    public void eliminarPorId(int id) {
        repositorioUsuario.deleteById(id);
    }

    @Override
    @Transactional
    public void cambiarContrasena(Usuario usuario, String contrasenaActual, String nuevaContrasena) {
        // Verificar contraseña actual
        if (!passwordEncoder.matches(contrasenaActual, usuario.getContrasena())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta.");
        }
        // Guardar nueva hasheada
        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        repositorioUsuario.save(usuario);
    }

    @Override
    @Transactional
    public void adminResetearContrasena(int idUsuario, String nuevaContrasena) {
        Usuario usuario = buscarPorId(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        repositorioUsuario.save(usuario);
    }
}