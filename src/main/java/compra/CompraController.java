package compra;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;

    @PostMapping("/cadastrar")
    @ResponseStatus(HttpStatus.CREATED)
    public CompraResponse cadastrar(@RequestBody @Valid CompraRequest request) {
        return compraService.cadastrarCompra(request);
    }

    @GetMapping("/listar")
    public Page<CompraResponse> listar(
            @RequestParam(required = false) EstadoCompra estado,
            @RequestParam(required = false) Long estoqueId,
            @PageableDefault(size = 10, sort = "dataCompra", direction = Sort.Direction.DESC) Pageable pageable) {
        return compraService.ListarCompras(estado, pageable, estoqueId);
    }

    @GetMapping("/buscar/{id}")
    public CompraResponse buscar(@PathVariable Long id) {
        return compraService.buscarCompra(id);
    }

    @PutMapping("/atualizar/{id}")
    public CompraResponse atualizar(@PathVariable Long id,
                                    @RequestBody @Valid CompraRequest request) {
        return compraService.atualizar(id, request);
    }

    @PatchMapping("/comprar/{id}")
    public CompraResponse comprar(@PathVariable Long id) {
        return compraService.comprar(id);
    }

    @PatchMapping("/cancelar/{id}")
    public CompraResponse cancelar(@PathVariable Long id) {
        return compraService.cancelarCompra(id);
    }
}