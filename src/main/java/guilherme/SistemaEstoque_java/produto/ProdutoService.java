package guilherme.SistemaEstoque_java.produto;

import guilherme.SistemaEstoque_java.categoria.Categoria;
import guilherme.SistemaEstoque_java.categoria.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private  final CategoriaRepository categoriaRepository;

    public Produto converterProduto(ProdutoRequest request) {
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId()).orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        Produto produto = new Produto();
        produto.setNome(request.getNome());
        produto.setCategoria(categoria);
        produto.setCodigo(request.getCodigo());
        produto.setValor(request.getValor());


        return produto;
    }

    public ProdutoResponse converterResponse(Produto produto) {
        ProdutoResponse response = new ProdutoResponse();

        response.setId(produto.getId());
        response.setNome(produto.getNome());
        response.setCategoriaId(produto.getCategoria().getId());
        response.setCodigo(produto.getCodigo());
        response.setValor(produto.getValor());
        response.setAtivo(produto.isAtivo());

        return response;
    }

    public ProdutoResponse cadastrarProduto(ProdutoRequest request) {
        if(produtoRepository.existsByCodigo(request.getCodigo())){
            throw  new RuntimeException("ja existe produto com esse codigo");
        }
        Produto produto = converterProduto(request);
        Produto novoProduto = produtoRepository.save(produto);

        return converterResponse(novoProduto);
    }

    public Page<ProdutoResponse> listarProduto(Pageable pageable) {
        return produtoRepository.findAll(pageable).map(this::converterResponse);
    }


    public ProdutoResponse buscarProduto(Long id) {
        Produto produto = produtoRepository.findById(id).orElseThrow(() -> new RuntimeException("Produto não existe"));

        return converterResponse(produto);
    }

    public Produto buscarEntidade(Long id) {
        return produtoRepository.findById(id).orElseThrow(() -> new RuntimeException("Produto não existe"));

    }

    public ProdutoResponse atualizarProduto(Long id, AtualizacaoRequest request) {
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId()).orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        Produto produto = buscarEntidade(id);
        produto.setNome(request.getNome());
        produto.setValor(request.getValor());
        produto.setCategoria(categoria);

        Produto produtoAtualizado = produtoRepository.save(produto);



        return converterResponse(produtoAtualizado);

    }

    public void ativarProduto(Long id) {
        Produto produto = buscarEntidade(id);

        produto.setAtivo(true);
        produtoRepository.save(produto);
    }

    public void desativarProduto(Long id) {
        Produto produto = buscarEntidade(id);

        produto.setAtivo(false);
        produtoRepository.save(produto);
    }
}
