package guilherme.SistemaEstoque_java.produto;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    boolean existsByCategoriaId(Long categoriaId);

    boolean existsByCodigo(String codigo);
}
