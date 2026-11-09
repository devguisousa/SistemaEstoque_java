package estoque;

import org.springframework.data.jpa.repository.JpaRepository;


public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    boolean existsByNomeAndProdutoId(String nome, Long produtoId);
    boolean existsByNomeAndProdutoIdAndIdNot(String nome, Long produtoId, Long id);

}
