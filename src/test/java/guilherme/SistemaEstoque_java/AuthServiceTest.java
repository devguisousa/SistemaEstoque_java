package guilherme.SistemaEstoque_java;

import guilherme.SistemaEstoque_java.auth.AuthService;
import guilherme.SistemaEstoque_java.auth.login.LoginRequest;
import guilherme.SistemaEstoque_java.auth.login.LoginResponse;
import guilherme.SistemaEstoque_java.jwt.JwtService;
import guilherme.SistemaEstoque_java.jwt.RefreshTokenService;
import guilherme.SistemaEstoque_java.usuario.AtualizarUsuarioRequest;
import guilherme.SistemaEstoque_java.usuario.TipoUsuario;
import guilherme.SistemaEstoque_java.usuario.Usuario;
import guilherme.SistemaEstoque_java.usuario.UsuarioRepository;
import guilherme.SistemaEstoque_java.usuario.UsuarioRequest;
import guilherme.SistemaEstoque_java.usuario.UsuarioResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;

    @BeforeEach
    void montar() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Ana");
        usuario.setEmail("ana@x.com");
        usuario.setSenha("senha-criptografada");
        usuario.setTipoUsuario(TipoUsuario.USUARIO);
    }

    @Test
    void cadastrarSalvaUsuarioComSenhaCriptografada() {
        UsuarioRequest request = new UsuarioRequest();
        request.setNome("Ana");
        request.setEmail("ana@x.com");
        request.setSenha("12345678");

        when(usuarioRepository.existsByEmail("ana@x.com")).thenReturn(false);
        when(passwordEncoder.encode("12345678")).thenReturn("senha-criptografada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);


        UsuarioResponse response = authService.cadastrar(request);

        assertThat(response.getEmail()).isEqualTo("ana@x.com");
        assertThat(response.getTipoUsuario()).isEqualTo(TipoUsuario.USUARIO);
        verify(passwordEncoder).encode("12345678");
    }

    @Test
    void cadastrarLancaErroQuandoEmailJaExiste() {
        UsuarioRequest request = new UsuarioRequest();
        request.setEmail("ana@x.com");

        when(usuarioRepository.existsByEmail("ana@x.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.cadastrar(request))
                .hasMessage("email ja cadastrado");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void loginDevolveAccessTokenERefreshToken() {
        LoginRequest request = new LoginRequest();
        request.setEmail("ana@x.com");
        request.setSenha("12345678");

        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(usuario, null));
        when(refreshTokenService.criar(usuario)).thenReturn("refresh-token");
        when(jwtService.gerar(usuario)).thenReturn("access-token");


        LoginResponse response = authService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getUsuario().getEmail()).isEqualTo("ana@x.com");
    }

    @Test
    void buscarLancaErroQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.buscar(99L))
                .hasMessage("usuario não encontrado");
    }

    @Test
    void atualizarCriptografaANovaSenha() {
        AtualizarUsuarioRequest request = new AtualizarUsuarioRequest();
        request.setSenha("novaSenha123");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("novaSenha123")).thenReturn("nova-senha-criptografada");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);


        authService.atualizar(1L, request);

        assertThat(usuario.getSenha()).isEqualTo("nova-senha-criptografada");
    }

    @Test
    void atualizarLancaErroQuandoEmailJaEDeOutroUsuario() {
        AtualizarUsuarioRequest request = new AtualizarUsuarioRequest();
        request.setEmail("bia@x.com");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmail("bia@x.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.atualizar(1L, request))
                .hasMessage("email ja cadastrado");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void refreshDevolveNovosTokens() {
        when(refreshTokenService.rotacionar("refresh-antigo"))
                .thenReturn(new RefreshTokenService.Rotacao(usuario, "refresh-novo"));
        when(jwtService.gerar(usuario)).thenReturn("access-novo");


        LoginResponse response = authService.refresh(new AuthService.RefreshRequest("refresh-antigo"));

        assertThat(response.getAccessToken()).isEqualTo("access-novo");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-novo");
    }

    @Test
    void logoutRevogaORefreshToken() {
        authService.logout(new AuthService.RefreshRequest("refresh-token"));

        verify(refreshTokenService).revogar("refresh-token");
    }
}
