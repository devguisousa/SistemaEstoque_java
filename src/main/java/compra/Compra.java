package compra;

import estoque.Estoque;
import fornecedor.Fornecedor;
import fornecedor.TipoFornecedor;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "compra")
@Getter
@Setter
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estoque_id")
    private Estoque estoque;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;

    @Min(1)
    @Column(nullable = false)
    private Integer quantidade;


    @Column(nullable = false)
    private LocalDate dataCompra;

    private LocalDate dataRecebimento;

    @Column(nullable = false, unique = true, updatable = false)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCompra estadoCompra = EstadoCompra.PENDENTE;

}
