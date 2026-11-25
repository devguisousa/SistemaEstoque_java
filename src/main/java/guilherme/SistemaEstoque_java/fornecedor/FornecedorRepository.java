package guilherme.SistemaEstoque_java.fornecedor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {


    boolean existsByEmail(String email);


    boolean existsByCpf(String cpf);

    Page<Fornecedor> findByTipo(TipoFornecedor tipo, Pageable pageable);

    Optional<Fornecedor> findByIdAndTipo(Long id, TipoFornecedor tipo);

    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByCpfAndIdNot(String cpf, Long id);
}
