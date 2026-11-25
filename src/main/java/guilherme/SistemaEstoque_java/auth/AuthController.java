package guilherme.SistemaEstoque_java.auth;

import guilherme.SistemaEstoque_java.auth.login.LoginRequest;
import guilherme.SistemaEstoque_java.auth.login.LoginResponse;
import guilherme.SistemaEstoque_java.usuario.UsuarioRequest;
import guilherme.SistemaEstoque_java.usuario.UsuarioResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/cadastrar")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse cadastrar(@RequestBody @Valid UsuarioRequest request) {
        return authService.cadastrar(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@RequestBody @Valid AuthService.RefreshRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody @Valid AuthService.RefreshRequest request) {
        authService.logout(request);
    }
}