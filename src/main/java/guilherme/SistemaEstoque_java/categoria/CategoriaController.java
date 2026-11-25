package guilherme.SistemaEstoque_java.categoria;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

@RequestMapping("Categoria")
@RequiredArgsConstructor
public class CategoriaController {
    private  final  CategoriaService service;

    @PostMapping("/cadastrar")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaResponse cadastrar(@RequestBody @Valid CategoriaRequest request) {
        return  service.cadastrarCategoria(request);
    }

    @GetMapping("/listar")
    public Page<CategoriaResponse> listar(@PageableDefault(sort = "nome", size = 10)Pageable pageable) {
        return  service.listarCategoria(pageable);
    }

    @GetMapping("/buscar/{id}")
    public CategoriaResponse buscar(@PathVariable Long id){
        return  service.buscarCategoria(id);
    }

    @PutMapping("/atualizar/{id}")
    public CategoriaResponse atualizar(@PathVariable Long id, @RequestBody @Valid CategoriaRequest request) {
        return  service.atualizarCategoria(id, request);
    }

    @DeleteMapping("/deletar/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.Deletar(id);
    }
}
