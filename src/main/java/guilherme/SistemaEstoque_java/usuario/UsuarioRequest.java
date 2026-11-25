package guilherme.SistemaEstoque_java.usuario;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRequest {

    @NotBlank(message = "Nome não pode ser vazio ou branco")
    private String nome;

    @Email
    @NotBlank(message = "email não pode ser vazio")
    private String email;

    @Size(min = 8, max = 72, message = "senha deve ser no mínimo 4 e no maximo 12 caracter")
    @NotBlank(message = "senha ão pode ser vazia")
    private String senha;
}
