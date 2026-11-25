package guilherme.SistemaEstoque_java.auth.login;

import lombok.Getter;
import lombok.Setter;
import guilherme.SistemaEstoque_java.usuario.UsuarioResponse;

@Getter
@Setter
public class LoginResponse {


    private String accessToken;
    private String refreshToken;
    private UsuarioResponse usuario;
}
