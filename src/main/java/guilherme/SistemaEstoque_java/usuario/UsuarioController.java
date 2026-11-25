package guilherme.SistemaEstoque_java.usuario;

import guilherme.SistemaEstoque_java.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final AuthService authService;

    @GetMapping
    public Page<UsuarioResponse> listar(Pageable pageable) {
        return authService.listar(pageable);
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscar(@PathVariable Long id) {
        return authService.buscar(id);
    }

    @PutMapping("/{id}")
    public UsuarioResponse atualizar(@PathVariable Long id,
                                     @RequestBody @Valid AtualizarUsuarioRequest request) {
        return authService.atualizar(id, request);
    }
}