package estoque;

import fornecedor.Fornecedor;
import fornecedor.TipoFornecedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    boolean existsByNomeAndProdutoId(String nome, Long produtoId);
    boolean existsByNomeAndProdutoIdAndIdNot(String nome, Long produtoId, Long id);

}
