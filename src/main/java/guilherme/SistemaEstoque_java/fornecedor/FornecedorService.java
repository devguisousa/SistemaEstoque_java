package guilherme.SistemaEstoque_java.fornecedor;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FornecedorService {
    private final FornecedorRepository fornecedorRepository;

    public Fornecedor converterFornecedor(FornecedorRequest request) {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setNome(request.getNome());
        fornecedor.setEmail(request.getEmail());
        fornecedor.setCpf(request.getCpf());
        fornecedor.setTelefone(request.getTelefone());
        fornecedor.setTipo(request.getTipo());

        return fornecedor;
    }

    public FornecedorResponse converterResponse(Fornecedor fornecedor) {
        FornecedorResponse response = new FornecedorResponse();
        response.setId(fornecedor.getId());
        response.setNome(fornecedor.getNome());
        response.setEmail(fornecedor.getEmail());
        response.setCpf(fornecedor.getCpf());
        response.setTelefone(fornecedor.getTelefone());
        response.setTipo(fornecedor.getTipo());
        response.setAtivo(fornecedor.isAtivo());

        return response;
    }

    public FornecedorResponse cadastrarFornecedor(FornecedorRequest request) {
        if (fornecedorRepository.existsByCpf(request.getCpf()) || fornecedorRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email ou cpf já existem");
        }

        Fornecedor fornecedor = converterFornecedor(request);
        return converterResponse(fornecedorRepository.save(fornecedor));
    }

    public Page<FornecedorResponse> listar(TipoFornecedor tipo, Pageable pageable) {
        Page<Fornecedor> pagina;
        if (tipo == null) {
            pagina = fornecedorRepository.findAll(pageable);

        } else {
            pagina = fornecedorRepository.findByTipo(tipo, pageable);
        }

        return pagina.map(this::converterResponse);
    }

    public FornecedorResponse buscarFornecedor(Long id, TipoFornecedor tipo) {
        Fornecedor fornecedor = fornecedorRepository.findByIdAndTipo(id, tipo).orElseThrow(() -> new RuntimeException("fornecedor não encontrado"));

        return converterResponse(fornecedor);

    }

    public FornecedorResponse atualizarFornecedor(Long id, FornecedorRequest request) {
        Fornecedor fornecedor = fornecedorRepository.findById(id).orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));

        if (fornecedorRepository.existsByCpfAndIdNot(request.getCpf(), id)
                || fornecedorRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new RuntimeException("Email ou cpf já existem em outro fornecedor");
        }
        fornecedor.setNome(request.getNome());
        fornecedor.setEmail(request.getEmail());
        fornecedor.setCpf(request.getCpf());
        fornecedor.setTelefone(request.getTelefone());
        fornecedor.setTipo(request.getTipo());
        fornecedor.setAtivo(fornecedor.isAtivo());

        return converterResponse(fornecedorRepository.save(fornecedor));
    }

    public FornecedorResponse desativarFornecedor(Long id) {
        Fornecedor fornecedor = fornecedorRepository.findById(id).orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
        fornecedor.setAtivo(false);

        return converterResponse(fornecedorRepository.save(fornecedor));
    }

    public FornecedorResponse ativarFornecedor(Long id) {
        Fornecedor fornecedor = fornecedorRepository.findById(id).orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
        fornecedor.setAtivo(true);
        return converterResponse(fornecedorRepository.save(fornecedor));
    }


}
