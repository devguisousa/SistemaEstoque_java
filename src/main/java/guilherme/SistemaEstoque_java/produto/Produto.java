package guilherme.SistemaEstoque_java.produto;

import guilherme.SistemaEstoque_java.categoria.Categoria;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
@Getter
@Setter
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "nome_produto")
    private String nome;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false, name = "valor_produto")
    private BigDecimal valor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @Column(nullable = false)
    private boolean ativo = true;



}