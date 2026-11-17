package transferencia;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class TransferenciaResponse {
    private Long id;

    private Long estoqueOrigem;

    private String origemNome;

    private Long estoqueDestino;

    private String destinoNome;

    private Integer quantidade;

    private String  codigo;

    private String produtoNome;

    private LocalDate dataTranferencia;

    private EstadoTransferencia estadoTransferencia;


}
