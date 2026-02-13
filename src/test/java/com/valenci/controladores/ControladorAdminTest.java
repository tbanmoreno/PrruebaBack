package com.valenci.controladores;

import com.valenci.config.JwtAuthenticationFilter;
import com.valenci.config.SecurityConfig;
import com.valenci.entidades.Cliente;
import com.valenci.entidades.Proveedor;
import com.valenci.entidades.Rol;
import com.valenci.entidades.Usuario;
import com.valenci.servicios.JwtService;
import com.valenci.servicios.ServicioUsuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ControladorAdmin.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
        })
@Import(SecurityConfig.class)
@ActiveProfiles("test")
@DisplayName("Pruebas para ControladorAdmin")
class ControladorAdminTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServicioUsuario servicioUsuario;

    @MockBean
    private JwtService jwtService;

    @Test
    @DisplayName("Debe devolver todos los usuarios cuando el usuario tiene autoridad ADMINISTRADOR")
    // CAMBIO CLAVE: Usamos authorities en lugar de roles para coincidir con .hasAuthority("ADMINISTRADOR")
    @WithMockUser(authorities = "ADMINISTRADOR")
    void obtenerTodosLosUsuarios_cuandoEsAdmin_debeDevolverListaDeUsuarios() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setId(1);
        cliente.setNombre("Cliente Test");
        cliente.setRol(Rol.valueOf("CLIENTE"));

        Proveedor proveedor = new Proveedor();
        proveedor.setId(2);
        proveedor.setNombre("Proveedor Test");
        proveedor.setRol(Rol.valueOf("PROVEEDOR"));

        List<Usuario> listaUsuarios = Arrays.asList(cliente, proveedor);
        when(servicioUsuario.listarTodos()).thenReturn(listaUsuarios);

        mockMvc.perform(get("/api/admin/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Cliente Test"))
                .andExpect(jsonPath("$[1].nombre").value("Proveedor Test"));
    }

    @Test
    @DisplayName("Debe denegar el acceso cuando el usuario es CLIENTE")
    @WithMockUser(authorities = "CLIENTE")
    void obtenerTodosLosUsuarios_cuandoNoEsAdmin_debeDevolverForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/usuarios"))
                .andExpect(status().isForbidden());
    }
}