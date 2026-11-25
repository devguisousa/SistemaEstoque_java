package guilherme.SistemaEstoque_java.compra;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompraResponse {

    private Long id;
    private String codigo;
    private Long estoqueId;
    private String estoqueNome;
    private String produtoNome;
    private Long fornecedorId;
    private String fornecedorNome;
    private Integer quantidade;
    private LocalDate dataCompra;
    private LocalDate dataRecebimento;
    private EstadoCompra estadoCompra;
}
