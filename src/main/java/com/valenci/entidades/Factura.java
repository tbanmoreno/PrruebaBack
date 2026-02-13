package com.valenci.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "facturas")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    private int idFactura;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido")
    // Evitamos la recursión: al serializar la factura, no necesitamos
    // que el pedido intente cargar de nuevo la factura o sus detalles pesados
    @JsonIgnoreProperties("detalles")
    private Pedido pedido;

    @Column(name = "fecha_factura")
    private LocalDateTime fechaFactura;

    @Column(name = "total_factura")
    private BigDecimal totalFactura;

    @Column(name = "iva")
    private BigDecimal iva;
}