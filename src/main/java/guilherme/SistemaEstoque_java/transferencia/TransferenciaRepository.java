package guilherme.SistemaEstoque_java.transferencia;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferenciaRepository extends JpaRepository<Transferencia, Long> {
        Page<Transferencia> findByEstadoTransferencia(EstadoTransferencia estadoTransferencia, Pageable pageable);
        Page<Transferencia> findByEstoqueOrigemId(Long estoqueId, Pageable pageable);
        Page<Transferencia> findByEstoqueDestinoId(Long estoqueId, Pageable pageable);

}
