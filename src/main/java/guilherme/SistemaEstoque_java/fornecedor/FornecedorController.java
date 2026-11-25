package guilherme.SistemaEstoque_java.fornecedor;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("fornecedores")
public class FornecedorController {

    private final FornecedorService fornecedorService;

    @PostMapping("/cadastrar")
    @ResponseStatus(HttpStatus.CREATED)
    public FornecedorResponse cadastrar(@RequestBody @Valid FornecedorRequest request) {
        return fornecedorService.cadastrarFornecedor(request);
    }

    @GetMapping("/listar")
    public Page<FornecedorResponse> listar(@RequestParam(required = false) TipoFornecedor tipo, @PageableDefault(sort = "nome", size = 10)Pageable pageable) {
        return fornecedorService.listar(tipo, pageable);
    }

    @GetMapping("/guilherme/SistemaEstoque_java/fornecedor/{id}")
    public FornecedorResponse buscarFornecedor(@PathVariable Long id) {
        return  fornecedorService.buscarFornecedor(id, TipoFornecedor.FORNECEDOR);
    }

    @GetMapping("/cliente/{id}")
    public FornecedorResponse buscarCliente(@PathVariable Long id) {
        return fornecedorService.buscarFornecedor(id, TipoFornecedor.CLIENTE);
    }

    @PutMapping("/atualizar/{id}")
    public FornecedorResponse atualizar(@PathVariable Long id, @RequestBody @Valid FornecedorRequest request) {
        return fornecedorService.atualizarFornecedor(id, request);
    }

    @PatchMapping("/ativar/{id}")
    public FornecedorResponse ativar(@PathVariable Long id) {
        return fornecedorService.ativarFornecedor(id);
    }

    @PatchMapping("/desativar/{id}")
    public  FornecedorResponse desativar(@PathVariable Long id) {
        return  fornecedorService.desativarFornecedor(id);
    }


}
