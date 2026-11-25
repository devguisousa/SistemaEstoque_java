package guilherme.SistemaEstoque_java.venda;

import guilherme.SistemaEstoque_java.estoque.Estoque;
import guilherme.SistemaEstoque_java.estoque.EstoqueRepository;
import guilherme.SistemaEstoque_java.fornecedor.Fornecedor;
import guilherme.SistemaEstoque_java.fornecedor.FornecedorRepository;
import guilherme.SistemaEstoque_java.fornecedor.TipoFornecedor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VendaService {
    private final VendaRepository vendaRepository;
    private final FornecedorRepository fornecedorRepository;
    private final EstoqueRepository estoqueRepository;

    public Venda converterVenda(VendaRequest request) {
        Fornecedor cliente = fornecedorRepository.findById(request.getClienteId()).orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        Estoque estoque = estoqueRepository.findById(request.getEstoqueId()).orElseThrow(() -> new RuntimeException("Estoque não encontrado"));

        Venda venda = new Venda();

        venda.setCliente(cliente);
        venda.setEstoque(estoque);
        venda.setQuantidade(request.getQuantidade());
        venda.setDataVenda(request.getDataVenda());
        venda.setCodigo("CODIGO - " + UUID.randomUUID().toString().substring(0, 8).toUpperCase());


        return venda;
    }

    public VendaResponse converterResponse(Venda venda) {
        VendaResponse response = new VendaResponse();

        response.setId(venda.getId());
        response.setClienteId(venda.getCliente().getId());
        response.setClienteNome(venda.getCliente().getNome());
        response.setEstoqueId(venda.getEstoque().getId());
        response.setEstoqueNome(venda.getEstoque().getNome());
        response.setProdutoNome(venda.getEstoque().getProduto().getNome());
        response.setCodigo(venda.getCodigo());
        response.setQuantidade(venda.getQuantidade());
        response.setDataVenda(venda.getDataVenda());
        response.setEstadoVenda(venda.getEstadoVenda());

        return response;
    }


    @Transactional
    public VendaResponse cadastrarVenda(VendaRequest request) {
        Venda venda = converterVenda(request);


        if (venda.getQuantidade() > venda.getEstoque().getQuantidade()) {
            throw new RuntimeException("quantidade da venda não pode ser maior do que a do estoque");
        }

        if (venda.getCliente().getTipo() != TipoFornecedor.CLIENTE) {
            throw new RuntimeException("Cliente inválido");
        }


        return converterResponse(vendaRepository.save(venda));
    }

    @Transactional(readOnly = true)
    public Page<VendaResponse> listarVendas(EstadoVenda venda, Pageable pageable) {
        Page<Venda> pagina;

        if (venda == null) {
            pagina = vendaRepository.findAll(pageable);
        } else {
            pagina = vendaRepository.findByEstadoVenda(venda, pageable);
        }

        return pagina.map(this::converterResponse);
    }

    @Transactional(readOnly = true)
    public VendaResponse buscarVendas(Long id) {
        Venda venda = vendaRepository.findById(id).orElseThrow(() -> new RuntimeException("Venda não encontrado"));

        return converterResponse(venda);
    }

    @Transactional
    public VendaResponse atualizarVendas(Long id, VendaRequest request) {
        Venda venda = vendaRepository.findById(id).orElseThrow(() -> new RuntimeException("Venda não encontrada"));
        Fornecedor cliente = fornecedorRepository.findById(request.getClienteId()).orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        Estoque estoque = estoqueRepository.findById(request.getEstoqueId()).orElseThrow(() -> new RuntimeException("Estoque não encontrado"));

        if (request.getQuantidade() > estoque.getQuantidade()) {
            throw new RuntimeException("quantidade da venda não pode ser maior do que a do estoque");
        }

        if (cliente.getTipo() != TipoFornecedor.CLIENTE) {
            throw new RuntimeException("cliente inválido");
        }
        if (venda.getEstadoVenda() != EstadoVenda.PENDENTE) {
            throw new RuntimeException("Só é possível editar venda pendente");
        }


        venda.setCliente(cliente);
        venda.setEstoque(estoque);
        venda.setQuantidade(request.getQuantidade());
        venda.setDataVenda(request.getDataVenda());

        return converterResponse(vendaRepository.save(venda));

    }

    @Transactional
    public VendaResponse cancelarVenda(Long id) {
        Venda venda = vendaRepository.findById(id).orElseThrow(() -> new RuntimeException("Venda não encontrada"));
        if (venda.getEstadoVenda() != EstadoVenda.PENDENTE) {
            throw new RuntimeException("Venda so pode ser cancelada se for pendente ");
        }
        venda.setEstadoVenda(EstadoVenda.CANCELADO);

        return converterResponse(vendaRepository.save(venda));
    }

    @Transactional
    public VendaResponse vender(Long id) {
        Venda venda = vendaRepository.findById(id).orElseThrow(() -> new RuntimeException("Venda não encontrada"));
        Estoque estoque = venda.getEstoque();
        if (venda.getEstadoVenda() != EstadoVenda.PENDENTE) {
            throw new RuntimeException("Venda so pode ser vendido se for pendente ");
        }

        if (venda.getQuantidade() > estoque.getQuantidade()) {
            throw new RuntimeException("Estoque insuficiente");
        }

        estoque.setQuantidade(estoque.getQuantidade() - venda.getQuantidade());
        venda.setEstadoVenda(EstadoVenda.VENDIDO);


        return converterResponse(vendaRepository.save(venda));
    }
}


