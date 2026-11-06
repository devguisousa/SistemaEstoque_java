package estoque;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstoqueResponse {

    private Long id;
    private String nome;
    private Long produtoId;
    private String produtoNome;
    private Integer quantidade;
}