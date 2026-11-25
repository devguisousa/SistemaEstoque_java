package guilherme.SistemaEstoque_java.venda;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;



@RequiredArgsConstructor
@RequestMapping("/vendas")
@RestController
public class VendaController {

    private final VendaService vendaService;

    @PostMapping("/cadastrar")
    @ResponseStatus(HttpStatus.CREATED)
    public VendaResponse cadastrar(@RequestBody @Valid VendaRequest request) {
        return vendaService.cadastrarVenda(request);
    }
    @GetMapping("/listar")
    public Page<VendaResponse> listar(@RequestParam(required = false) EstadoVenda venda, @PageableDefault(size = 10, sort = "dataVenda", direction = Sort.Direction.DESC) Pageable pageable) {
        return vendaService.listarVendas(venda, pageable);
    }

    @GetMapping("/buscar/{id}")
    public  VendaResponse buscar(@PathVariable Long id) {
        return vendaService.buscarVendas(id);
    }

    @PutMapping("/atualizar/{id}")
    public VendaResponse atualizar(@PathVariable Long id, @RequestBody @Valid VendaRequest request) {
        return  vendaService.atualizarVendas(id, request);
    }

    @PatchMapping("/cancelar/{id}")
    public VendaResponse cancelar(@PathVariable Long id) {
        return vendaService.cancelarVenda(id);
    }

    @PatchMapping("/vender/{id}")
    public VendaResponse vender(@PathVariable Long id) {
        return vendaService.vender(id);
    }
}
