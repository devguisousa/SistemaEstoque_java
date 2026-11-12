package venda;

import estoque.Estoque;
import fornecedor.Fornecedor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;



import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "venda")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estoque_id")
    private Estoque estoque;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id")
    private Fornecedor cliente;

    @Column(nullable = false, unique = true, updatable = false)
    private String codigo;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private LocalDate dataVenda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVenda estadoVenda = EstadoVenda.PENDENTE;


}
