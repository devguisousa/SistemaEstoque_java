package guilherme.SistemaEstoque_java.transferencia;

import guilherme.SistemaEstoque_java.estoque.Estoque;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "transferencia")
public class Transferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estoque_origem_id")
    private Estoque estoqueOrigem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estoque_destino_id")
    private Estoque estoqueDestino;

    @Column(nullable = false, unique = true, updatable = false)
    private String codigo;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private LocalDate dataTransferencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTransferencia estadoTransferencia = EstadoTransferencia.PENDENTE;
}