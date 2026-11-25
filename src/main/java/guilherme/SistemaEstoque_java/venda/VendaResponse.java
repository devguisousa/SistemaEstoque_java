package guilherme.SistemaEstoque_java.venda;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class VendaResponse {

    private Long id;
    private Long estoqueId;
    private String estoqueNome;
    private Long clienteId;
    private String clienteNome;
    private String produtoNome;
    private String codigo;
    private Integer quantidade;
    private LocalDate dataVenda;
    private EstadoVenda estadoVenda;

}
