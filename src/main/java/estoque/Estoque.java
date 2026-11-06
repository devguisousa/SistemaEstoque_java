package estoque;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import produto.Produto;

@Getter
@Setter
@Entity
@Table(name = "estoque", uniqueConstraints = @UniqueConstraint(columnNames = {"nome, produto_id"}))
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @Min(0)
    @Column(nullable = false)
    private Integer quantidade;
}
