package fornecedor;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FornecedorResponse {

    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private TipoFornecedor tipo;
    private Boolean ativo;
}
