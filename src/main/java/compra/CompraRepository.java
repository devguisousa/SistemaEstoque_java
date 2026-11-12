package compra;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CompraRepository extends JpaRepository<Compra, Long> {

    Page<Compra> findByEstadoCompra(EstadoCompra estadoCompra, Pageable pageable);

    Page<Compra> findByEstoqueId(Long estoqueId, Pageable pageable);


}
