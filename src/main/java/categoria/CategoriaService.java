package categoria;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import produto.ProdutoRepository;


@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;

    public Categoria converterCategoria(CategoriaRequest request) {
        Categoria categoria = new Categoria();

        categoria.setNome(request.nome());

        return categoria;
    }

    public CategoriaResponse converterResponse(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome()

        );
    }

    public CategoriaResponse cadastrarCategoria(CategoriaRequest request) {
        Categoria categoria = converterCategoria(request);
        Categoria novaCategoria = categoriaRepository.save(categoria);

        return converterResponse(novaCategoria);
    }

    public Page<CategoriaResponse> listarCategoria(Pageable pageable) {
        return categoriaRepository.findAll(pageable).map(this::converterResponse);
    }

    public CategoriaResponse buscarCategoria(Long id) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() -> new RuntimeException("Categoria não existe"));

        return converterResponse(categoria);
    }


    public CategoriaResponse atualizarCategoria(Long id, CategoriaRequest request) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        categoria.setNome(request.nome());

        return converterResponse(categoriaRepository.save(categoria));
    }

    public void Deletar(Long id) {
        if (produtoRepository.existsByProduto(id)) {
            throw  new RuntimeException("Categoria tem produtos registrados e não pode ser apagada");
        }
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        categoriaRepository.delete(categoria);
    }

}
