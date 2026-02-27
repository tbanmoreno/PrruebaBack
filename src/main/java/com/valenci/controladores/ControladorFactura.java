package com.valenci.controladores;

import com.valenci.dto.DtoRespuestaFactura;
import com.valenci.mapper.MapeadorPedido; // Usamos el mapeador unificado
import com.valenci.servicios.ServicioFactura;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Cambiado a * para facilitar pruebas en desarrollo
public class ControladorFactura {

    private final ServicioFactura servicioFactura;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<DtoRespuestaFactura>> listarTodas() {
        List<DtoRespuestaFactura> respuesta = servicioFactura.listarTodas().stream()
                .map(MapeadorPedido::aDtoFactura) // Cambio a MapeadorPedido
                .collect(Collectors.toList());
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<DtoRespuestaFactura> obtenerPorId(@PathVariable int id) {
        return servicioFactura.buscarPorId(id)
                .map(MapeadorPedido::aDtoFactura)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Factura no encontrada"));
    }

    @GetMapping("/pedido/{idPedido}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<DtoRespuestaFactura> obtenerPorIdPedido(@PathVariable int idPedido) {
        return servicioFactura.buscarPorIdPedido(idPedido)
                .map(MapeadorPedido::aDtoFactura)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe factura para este pedido"));
    }
}