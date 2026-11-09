package compra;

import estoque.Estoque;
import estoque.EstoqueRepository;
import fornecedor.Fornecedor;
import fornecedor.FornecedorRepository;
import fornecedor.TipoFornecedor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class CompraService {
    private final CompraRepository compraRepository;
    private final FornecedorRepository fornecedorRepository;
    private final EstoqueRepository estoqueRepository;

    public Compra converterCompra(CompraRequest request) {
        Fornecedor fornecedor = fornecedorRepository.findById(request.getFornecedorId()).orElseThrow(() -> new RuntimeException("Fornecerdor não encontrado"));
        Estoque estoque = estoqueRepository.findById(request.getEstoqueId()).orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (fornecedor.getTipo() != TipoFornecedor.FORNECEDOR) {
            throw new RuntimeException("Fornecedor inválido");
        }

        if (request.getDataRecebimento() != null
                && request.getDataRecebimento().isBefore(request.getDataCompra())) {
            throw new RuntimeException("O recebimento não pode ser antes da data da compra");
        }

        Compra compra = new Compra();

        compra.setFornecedor(fornecedor);
        compra.setEstoque(estoque);
        compra.setCodigo("CODIGO - " + UUID.randomUUID().toString().substring(0,8).toUpperCase());
        compra.setQuantidade(request.getQuantidade());
        compra.setDataCompra(request.getDataCompra());
        compra.setDataRecebimento(request.getDataRecebimento());

        return compra;
    }

    public CompraResponse converterResponse(Compra compra) {
        CompraResponse response = new CompraResponse();
        response.setId(compra.getId());
        response.setCodigo(compra.getCodigo());
        response.setEstoqueId(compra.getEstoque().getId());
        response.setEstoqueNome(compra.getEstoque().getNome());
        response.setProdutoNome(compra.getEstoque().getProduto().getNome());
        response.setFornecedorId(compra.getFornecedor().getId());
        response.setFornecedorNome(compra.getFornecedor().getNome());
        response.setQuantidade(compra.getQuantidade());
        response.setDataCompra(compra.getDataCompra());
        response.setDataRecebimento(compra.getDataRecebimento());
        response.setEstadoCompra(compra.getEstadoCompra());

        return response;

    }

    @Transactional
    public CompraResponse cadastrarCompra(CompraRequest request) {


        Compra compra = converterCompra(request);

        return converterResponse(compraRepository.save(compra));
    }

    @Transactional(readOnly = true)
    public Page<CompraResponse> ListarCompras(EstadoCompra estadoCompra, Pageable pageable, Long estoqueId) {
        Page<Compra> pagina;

        if (estadoCompra != null) {
            pagina = compraRepository.findByEstadoCompra(estadoCompra, pageable);
        } else if (estoqueId != null) {
            pagina = compraRepository.findByEstoqueId(estoqueId, pageable);

        } else {
            pagina = compraRepository.findAll(pageable);
        }

        return pagina.map(this::converterResponse);
    }
    @Transactional(readOnly = true)
    public CompraResponse buscarCompra(Long id) {
        Compra compra = compraRepository.findById(id).orElseThrow(() -> new RuntimeException("Compra não encotrado"));

        return converterResponse(compra);
    }

    @Transactional
    public CompraResponse atualizar(Long id, CompraRequest request) {
        Compra compra = compraRepository.findById(id).orElseThrow(() -> new RuntimeException("Compra não encotrado"));
        Fornecedor fornecedor = fornecedorRepository.findById(request.getFornecedorId()).orElseThrow(() -> new RuntimeException("Fornecerdor não encontrado"));
        Estoque estoque = estoqueRepository.findById(request.getEstoqueId()).orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (fornecedor.getTipo() != TipoFornecedor.FORNECEDOR) {
            throw new RuntimeException("Fornecedor inválido");
        }

        if (request.getDataRecebimento() != null
                && request.getDataRecebimento().isBefore(request.getDataCompra())) {
            throw new RuntimeException("O recebimento não pode ser antes da data da compra");
        }

        if (compra.getEstadoCompra()  != EstadoCompra.PENDENTE) {
            throw new RuntimeException("Compra cancelada não pode ser editada");
        }


        compra.setEstoque(estoque);
        compra.setFornecedor(fornecedor);
        compra.setQuantidade(request.getQuantidade());
        compra.setDataCompra(request.getDataCompra());
        compra.setDataRecebimento(request.getDataRecebimento());

        return converterResponse(compra);
    }

    @Transactional
    public CompraResponse cancelarCompra(Long id) {
        Compra compra = compraRepository.findById(id).orElseThrow(() -> new RuntimeException("Compra não encotrado"));

        if (compra.getEstadoCompra() != EstadoCompra.PENDENTE) {
            throw new RuntimeException("Só é possivel cancelar compra pendete");
        }

        compra.setEstadoCompra(EstadoCompra.CANCELADO);

        return converterResponse(compra);
    }

    @Transactional
    public CompraResponse comprar(Long id) {
        Compra compra = compraRepository.findById(id).orElseThrow(() -> new RuntimeException("Compra não encotrado"));
        if (compra.getEstadoCompra() != EstadoCompra.PENDENTE) {
            throw new RuntimeException("so é possivel comprar conta pedente");
        }

        Estoque estoque = compra.getEstoque();

        estoque.setQuantidade(estoque.getQuantidade() + compra.getQuantidade());

        compra.setEstadoCompra(EstadoCompra.COMPRADO);

        return converterResponse(compraRepository.save(compra));
    }
}


