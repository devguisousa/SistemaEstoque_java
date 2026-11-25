package guilherme.SistemaEstoque_java.estoque;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/estoque")
public class EstoqueController {
    private final  EstoqueService estoqueService;

    @PostMapping("/cadastrar")
    public EstoqueResponse response(@RequestBody @Valid EstoqueRequest request) {
        return estoqueService.cadastrarEstoque(request);
    }

    @GetMapping("/listar")
    public Page<EstoqueResponse> listar(@PageableDefault(sort = "nome", size = 10)Pageable pageable) {
        return estoqueService.listarEstoque(pageable);
    }

    @GetMapping("/buscar/{id}")
    public EstoqueResponse buscar(@PathVariable Long id) {
    return estoqueService.buscarEstoque(id);
    }

    @PutMapping("/atualizar/{id}")
    public  EstoqueResponse atualizar(@PathVariable Long id, @RequestBody @Valid EstoqueRequest request) {
        return estoqueService.atualizarEstoque(id, request);
    }

    @DeleteMapping("/deletar/{id}")
    public void deletar(@PathVariable Long id) {
        estoqueService.deletarEstoque(id);
    }
}
