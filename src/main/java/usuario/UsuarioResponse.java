package usuario;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioResponse {

    private Long id;

    private String email;

    private String nome;

    private TipoUsuario tipoUsuario;
}
