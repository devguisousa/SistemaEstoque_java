package auth;

import auth.login.LoginResponse;
import jakarta.validation.Valid;
import jwt.JwtService;
import jwt.RefreshToken.RefreshRequest;
import jwt.RefreshToken.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import auth.login.LoginRequest;
import usuario.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshService;

    @PostMapping("/cadastrar")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse cadastrar(@RequestBody @Valid UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Já existe um usuário com esse email");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setTipoUsuario(TipoUsuario.USUARIO);

        return converterResponse(usuarioRepository.save(usuario));
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        var credenciais = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha());
        Authentication autenticacao = authenticationManager.authenticate(credenciais);
        Usuario usuario = (Usuario) autenticacao.getPrincipal();

        return montarTokens(usuario, refreshService.criar(usuario));
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@RequestBody @Valid RefreshRequest request) {
        RefreshTokenService.Rotacao rotacao = refreshService.rotacionar(request.getRefreshToken());

        return montarTokens(rotacao.usuario(), rotacao.novoRefreshToken());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody @Valid RefreshRequest request) {
        refreshService.revogar(request.getRefreshToken());
    }

    private LoginResponse montarTokens(Usuario usuario, String refreshToken) {
        LoginResponse response = new LoginResponse();
        response.setAccessToken(jwtService.gerar(usuario));
        response.setRefreshToken(refreshToken);
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
}