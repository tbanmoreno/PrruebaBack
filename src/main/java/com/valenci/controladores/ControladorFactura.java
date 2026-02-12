package com.valenci.controladores;

import com.valenci.dto.DtoRespuestaFactura;
import com.valenci.mapper.MapeadorFactura;
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
@CrossOrigin(origins = "http://localhost:5173")
public class ControladorFactura {

    private final ServicioFactura servicioFactura;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<DtoRespuestaFactura>> listarTodas() {
        // Obtenemos las entidades y transformamos a DTOs planos para React
        List<DtoRespuestaFactura> respuesta = servicioFactura.listarTodas().stream()
                .map(MapeadorFactura::aDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<DtoRespuestaFactura> obtenerPorId(@PathVariable int id) {
        return servicioFactura.buscarPorId(id)
                .map(MapeadorFactura::aDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Factura no encontrada"));
    }
}