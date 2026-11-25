package guilherme.SistemaEstoque_java.usuario;


import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class AtualizarUsuarioRequest {

    @Email(message = "Email inválido")
    private String email;

    @Length(min = 8)
    private String senha;
}
