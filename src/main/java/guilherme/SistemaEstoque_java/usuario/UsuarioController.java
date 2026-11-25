package guilherme.SistemaEstoque_java.usuario;

import guilherme.SistemaEstoque_java.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {


    private static final String DONO_OU_ADMIN = "hasRole('ADMIM') or #id == authentication.principal.id";

    private final AuthService authService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIM')")
    public Page<UsuarioResponse> listar(Pageable pageable) {
        return authService.listar(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize(DONO_OU_ADMIN)
    public UsuarioResponse buscar(@PathVariable Long id) {
        return authService.buscar(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize(DONO_OU_ADMIN)
    public UsuarioResponse atualizar(@PathVariable Long id,
                                     @RequestBody @Valid AtualizarUsuarioRequest request) {
        return authService.atualizar(id, request);
    }
}