package guilherme.SistemaEstoque_java.usuario;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class AtualizarUsuarioRequest {

    private String email;

    @Length(min = 8)
    private String senha;
}
