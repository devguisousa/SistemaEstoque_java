package guilherme.SistemaEstoque_java.produto;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    boolean existsByProduto(Long id);
    boolean existsByCodigo(String codigo);
}
