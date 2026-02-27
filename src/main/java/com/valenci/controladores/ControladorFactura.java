package com.valenci.controladores;

import com.valenci.dto.DtoRespuestaFactura;
import com.valenci.mapper.MapeadorPedido; // <--- IMPORTANTE: Usamos este mapeador
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
@CrossOrigin(origins = "*")
public class ControladorFactura {

    private final ServicioFactura servicioFactura;

    @GetMapping("/pedido/{idPedido}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<DtoRespuestaFactura> obtenerPorIdPedido(@PathVariable int idPedido) {
        // El Error 500 suele ocurrir aquí por datos nulos o mapeadores incorrectos
        return servicioFactura.buscarPorIdPedido(idPedido)
                .map(factura -> {
                    try {
                        return MapeadorPedido.aDtoFactura(factura);
                    } catch (Exception e) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al mapear la factura");
                    }
                })
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe factura para este pedido"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<DtoRespuestaFactura>> listarTodas() {
        List<DtoRespuestaFactura> respuesta = servicioFactura.listarTodas().stream()
                .map(MapeadorPedido::aDtoFactura)
                .collect(Collectors.toList());
        return ResponseEntity.ok(respuesta);
    }
}