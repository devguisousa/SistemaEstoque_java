package guilherme.SistemaEstoque_java.produto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("produtos")
@RequiredArgsConstructor
public class ProdutoController {
    private  final ProdutoService produtoService;

    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoResponse cadastrar(@RequestBody @Valid ProdutoRequest request) {
        return produtoService.cadastrarProduto(request);

    }

    @GetMapping("/listar")
    public Page<ProdutoResponse> listar(@PageableDefault(size = 10, sort = "nome")Pageable pageable) {
        return produtoService.listarProduto(pageable);
    }

    @GetMapping("/buscar/{id}")
    public ProdutoResponse buscar(@PathVariable Long id) {
        return produtoService.buscarProduto(id);
    }

    @PutMapping("/atualizar/{id}")
    public ProdutoResponse atualizar( @PathVariable Long id, @RequestBody @Valid AtualizacaoRequest request) {
        return produtoService.atualizarProduto(id, request);
    }

    @PatchMapping("/ativar/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void ativar(@PathVariable Long id) {
        produtoService.ativarProduto(id);
    }

    @PatchMapping("/desativar/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable Long id) {
        produtoService.desativarProduto(id);
    }
}

