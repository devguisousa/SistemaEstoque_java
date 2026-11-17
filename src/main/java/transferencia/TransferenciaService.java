package transferencia;

import estoque.Estoque;
import estoque.EstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferenciaService {

    private final TransferenciaRepository transferenciaRepository;
    private final EstoqueRepository estoqueRepository;

    public Transferencia converterTransferencia(TransferenciaRequest request) {
        Estoque origem = estoqueRepository.findById(request.getEstoqueOrigemId()).orElseThrow(() -> new RuntimeException("Estoque não encontrado"));
        Estoque destino = estoqueRepository.findById(request.getEstoqueDestinoId()).orElseThrow(() -> new RuntimeException("Estoque não encontrado"));


        if(origem.getId().equals(destino.getId())) {
            throw new RuntimeException("origem não pode ser a mesma que o destino");
        }

        if(!origem.getProduto().getId().equals(destino.getProduto().getId())) {
            throw new RuntimeException("produto de origem precisa ser igual ao destino");
        }

        Transferencia transferencia = new Transferencia();

        transferencia.setCodigo("CODIGO - " + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        transferencia.setEstoqueOrigem(origem);
        transferencia.setEstoqueDestino(destino);
        transferencia.setQuantidade(request.getQuantidade());
        transferencia.setDataTransferencia(request.getDataTransferencia());
        return transferencia;
    }

    public TransferenciaResponse converterResponse(Transferencia transferencia) {
        TransferenciaResponse response = new TransferenciaResponse();

        response.setId(transferencia.getId());
        response.setCodigo(transferencia.getCodigo());
        response.setEstoqueOrigem(transferencia.getEstoqueOrigem().getId());
        response.setOrigemNome(transferencia.getEstoqueOrigem().getNome());
        response.setEstoqueDestino(transferencia.getEstoqueDestino().getId());
        response.setDestinoNome(transferencia.getEstoqueDestino().getNome());
        response.setQuantidade(transferencia.getQuantidade());
        response.setProdutoNome(transferencia.getEstoqueOrigem().getProduto().getNome());
        response.setDataTranferencia(transferencia.getDataTransferencia());
        response.setEstadoTransferencia(transferencia.getEstadoTransferencia());

        return response;
    }

    @Transactional
    public TransferenciaResponse cadastrarTransferencia(TransferenciaRequest request) {

        Transferencia transferencia = converterTransferencia(request);

        return converterResponse(transferenciaRepository.save(transferencia));
    }


    @Transactional(readOnly = true)
    public Page<TransferenciaResponse> listarTransferencias(Long estoqueDestinoId, Long estoqueOrigemId, EstadoTransferencia estadoTransferencia, Pageable pageable) {
        Page<Transferencia> pagina;
        if (estadoTransferencia != null) {
            pagina = transferenciaRepository.findByEstadoTransferencia(estadoTransferencia, pageable);
        } else if (estoqueDestinoId != null) {
            pagina = transferenciaRepository.findByEstoqueDestinoId(estoqueDestinoId, pageable);

        } else if (estoqueOrigemId != null) {
            pagina = transferenciaRepository.findByEstoqueOrigemId(estoqueOrigemId, pageable);
        } else {
            pagina = transferenciaRepository.findAll(pageable);
        }
        return pagina.map(this::converterResponse);
    }

    @Transactional(readOnly = true)
    public TransferenciaResponse buscarTransferencia(Long id) {
        Transferencia transferencia = transferenciaRepository.findById(id).orElseThrow(() -> new RuntimeException("Transferencia não encontrada"));

        return converterResponse(transferencia);
    }


    @Transactional
    public TransferenciaResponse atualizarTransferencia(Long id, TransferenciaRequest request) {
        Transferencia transferencia = transferenciaRepository.findById(id).orElseThrow(() -> new RuntimeException("Transferencia não encontrada"));

        if(transferencia.getEstadoTransferencia() != EstadoTransferencia.PENDENTE) {
            throw  new RuntimeException("Transferencia não pode ser alterado pelo metado atualizar");
        }

        Estoque origem = estoqueRepository.findById(request.getEstoqueOrigemId()).orElseThrow(() -> new RuntimeException("origem não encontrado"));
        Estoque destino = estoqueRepository.findById(request.getEstoqueDestinoId()).orElseThrow(() -> new RuntimeException("destino não encontrado"));

        if(origem.getId().equals(destino.getId())) {
            throw new RuntimeException("origem não pode ser a mesma que o destino");
        }

        if(!origem.getProduto().getId().equals(destino.getProduto().getId())) {
            throw new RuntimeException("produto de origem precisa ser igual ao destino");
        }

        transferencia.setQuantidade(request.getQuantidade());
        transferencia.setDataTransferencia(request.getDataTransferencia());
        transferencia.setEstoqueOrigem(origem);
        transferencia.setEstoqueDestino(destino);

        return converterResponse(transferencia);
    }

    @Transactional
    public TransferenciaResponse transferir(Long id) {
        Transferencia transferencia = transferenciaRepository.findById(id).orElseThrow(() -> new RuntimeException("Transferencia não encontrada"));
        if (transferencia.getEstadoTransferencia() != EstadoTransferencia.PENDENTE) {
            throw new RuntimeException("Transferencia so pode ser transferida caso seja pendente");
        }
        transferencia.setEstadoTransferencia(EstadoTransferencia.TRANSFERIDO);

        return converterResponse(transferencia);
    }

    @Transactional
    public TransferenciaResponse cancelarTransferencia(Long id) {
        Transferencia transferencia = transferenciaRepository.findById(id).orElseThrow(() -> new RuntimeException("Transferencia não encontrada"));
        if (transferencia.getEstadoTransferencia() != EstadoTransferencia.PENDENTE) {
            throw new RuntimeException("Transferencia so pode ser cancelada caso seja pendente");
        }

        transferencia.setEstadoTransferencia(EstadoTransferencia.CANCELADO);

        return converterResponse(transferencia);

    }


}
