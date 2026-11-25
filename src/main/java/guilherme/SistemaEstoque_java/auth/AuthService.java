package guilherme.SistemaEstoque_java.auth;

import guilherme.SistemaEstoque_java.auth.login.LoginRequest;
import guilherme.SistemaEstoque_java.auth.login.LoginResponse;
import guilherme.SistemaEstoque_java.jwt.JwtService;
import guilherme.SistemaEstoque_java.jwt.RefreshTokenService;
import guilherme.SistemaEstoque_java.usuario.*;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public record RefreshRequest(@NotBlank String refreshToken) {
    }

    public UsuarioResponse cadastrar(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("email ja cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail().trim().toLowerCase());
        usuario.setNome(request.getNome());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setTipoUsuario(TipoUsuario.USUARIO);

        return converterResponse(usuarioRepository.save(usuario));

    }

    public Page<UsuarioResponse> listar(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(this::converterResponse);
    }

    public UsuarioResponse buscar(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("usuario não encontrado"));

        return converterResponse(usuario);
    }

    public UsuarioResponse atualizar(Long id, AtualizarUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("usuario não encontrado"));


        if (request.getEmail() != null) {
            String email = request.getEmail().trim().toLowerCase();

            if (!email.equals(usuario.getEmail())) {
                if (usuarioRepository.existsByEmail(email)) {
                    throw new RuntimeException("email ja cadastrado");
                }
            }

            usuario.setEmail(email);
        }

        String senha = request.getSenha();
        if (senha != null) {
            usuario.setSenha(passwordEncoder.encode(senha));
        }


        return converterResponse(usuarioRepository.save(usuario));

    }

    public LoginResponse login(LoginRequest request) {
        var credenciais = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha());
        Authentication authentication = authenticationManager.authenticate(credenciais);
        Usuario usuario = (Usuario) authentication.getPrincipal();

        String refreshToken = refreshTokenService.criar(usuario);

        return montarLoginResponse(usuario, refreshToken);
    }

    private LoginResponse montarLoginResponse(Usuario usuario, String refreshToken) {
        LoginResponse response = new LoginResponse();
        response.setAccessToken(jwtService.gerar(usuario));
        response.setRefreshToken(refreshToken);
        response.setUsuario(converterResponse(usuario));
        return response;
    }

    private UsuarioResponse converterResponse(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setNome(usuario.getNome());
        response.setEmail(usuario.getEmail());
        response.setTipoUsuario(usuario.getTipoUsuario());
        return response;
    }

    public LoginResponse refresh(RefreshRequest request) {
        var rotacao = refreshTokenService.rotacionar(request.refreshToken);

        return montarLoginResponse(rotacao.usuario(), rotacao.novoRefreshToken());
    }

    public void logout(RefreshRequest request) {
        refreshTokenService.revogar(request.refreshToken);
    }

}
