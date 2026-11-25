package guilherme.SistemaEstoque_java.produto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProdutoResponse {
    private Long id;
    private Long categoriaId;
    private String nome;
    private String codigo;

    private BigDecimal valor;

    private Boolean ativo;
}
