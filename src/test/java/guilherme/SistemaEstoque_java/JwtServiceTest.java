package guilherme.SistemaEstoque_java;

import guilherme.SistemaEstoque_java.jwt.JwtService;
import guilherme.SistemaEstoque_java.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;
    private Usuario usuario;

    @BeforeEach
    void montar() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "chave-so-de-teste-com-mais-de-32-caracteres");

        usuario = new Usuario();
        usuario.setEmail("ana@x.com");
    }

    @Test
    void validarDevolveOEmailDoTokenGerado() {
        String token = jwtService.gerar(usuario);
        String email = jwtService.validar(token);

        assertThat(email).isEqualTo("ana@x.com");
    }

    @Test
    void gerarDevolveTokenComTresPartes() {
        String token = jwtService.gerar(usuario);

        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void validarDevolveNuloParaTokenInvalido() {
        String email = jwtService.validar("token-qualquer");

        assertThat(email).isNull();
    }

    @Test
    void validarDevolveNuloParaTokenAlterado() {
        String token = jwtService.gerar(usuario);
        String email = jwtService.validar(token + "a");

        assertThat(email).isNull();
    }

    @Test
    void validarDevolveNuloParaTokenDeOutraChave() {
        JwtService outroJwtService = new JwtService();
        ReflectionTestUtils.setField(outroJwtService, "secret", "outra-chave-de-teste-com-mais-de-32-caracteres");

        String token = outroJwtService.gerar(usuario);
        String email = jwtService.validar(token);

        assertThat(email).isNull();
    }
}
