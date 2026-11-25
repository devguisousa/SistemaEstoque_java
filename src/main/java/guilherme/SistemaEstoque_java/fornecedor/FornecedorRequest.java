package guilherme.SistemaEstoque_java.fornecedor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FornecedorRequest {
    @NotBlank(message = "O nome não pode ser vazio")
    private String nome;

    @NotBlank(message = "O email não pode ser vazio")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "O CPF não pode ser vazio")
    @Pattern(regexp = "\\d{11}", message = "O CPF deve ter 11 números, sem pontos nem traço")
    private String cpf;

    @NotBlank(message = "O telefone não pode ser vazio")
    @Pattern(regexp = "\\d{10,11}", message = "O telefone deve ter DDD e 10 ou 11 números")
    private String telefone;

    @NotNull(message = "O tipo é obrigatório")
    private TipoFornecedor tipo;
}
