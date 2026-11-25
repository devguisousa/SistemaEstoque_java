package guilherme.SistemaEstoque_java.estoque;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import guilherme.SistemaEstoque_java.produto.Produto;
import guilherme.SistemaEstoque_java.produto.ProdutoRepository;

@Service
@RequiredArgsConstructor
public class EstoqueService {
    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;

    public Estoque converterEstoque(EstoqueRequest request) {
        Produto produto = produtoRepository.findById(request.getProdutoId()).orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Estoque estoque = new Estoque();
        estoque.setNome(request.getNome());
        estoque.setProduto(produto);
        estoque.setQuantidade(request.getQuantidade());

        return estoque;
    }

    public EstoqueResponse converterResponse(Estoque estoque) {
        EstoqueResponse response = new EstoqueResponse();
        response.setId(estoque.getId());
        response.setNome(estoque.getNome());
        response.setProdutoId(estoque.getProduto().getId());
        response.setProdutoNome(estoque.getProduto().getNome());

        return response;
    }

    public EstoqueResponse cadastrarEstoque(EstoqueRequest request) {
        if (estoqueRepository.existsByNomeAndProdutoId(request.getNome(), request.getProdutoId())) {
            throw new RuntimeException("Já existe estoque desse produto com esse nome");
        }


        Estoque estoque = converterEstoque(request);


        Estoque novoEstoque = estoqueRepository.save(estoque);

        return converterResponse(novoEstoque);

    }

    public Page<EstoqueResponse> listarEstoque(Pageable pageable) {
        return estoqueRepository.findAll(pageable).map(this::converterResponse);
    }

    public EstoqueResponse buscarEstoque(Long id) {
        Estoque estoque = estoqueRepository.findById(id).orElseThrow(() -> new RuntimeException("Estoque não existe"));

        return converterResponse(estoque);
    }

    public EstoqueResponse atualizarEstoque(Long id, EstoqueRequest request) {
        Estoque estoque = estoqueRepository.findById(id).orElseThrow(() -> new RuntimeException("Estoque não encontrado"));
        Produto produto = produtoRepository.findById(request.getProdutoId()).orElseThrow(() -> new RuntimeException("produto não encontrado"));

        if (estoqueRepository.existsByNomeAndProdutoIdAndIdNot(
                request.getNome(), request.getProdutoId(), id)) {
            throw new RuntimeException("Já existe estoque desse produto com esse nome");
        }


        estoque.setNome(request.getNome());
        estoque.setQuantidade(request.getQuantidade());
        estoque.setProduto(produto);
        return converterResponse(estoqueRepository.save(estoque));
    }

    public void deletarEstoque(Long id) {
        Estoque estoque = estoqueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estoque não encontrado"));

        if (estoque.getQuantidade() > 0) {
            throw new RuntimeException("Não é possível apagar um estoque com quantidade maior que zero");
        }

        estoqueRepository.delete(estoque);
    }


}
