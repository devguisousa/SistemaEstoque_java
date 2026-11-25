package guilherme.SistemaEstoque_java.venda;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class VendaRequest {

    @NotNull(message = "Estoque é obrigátorio")
    private Long estoqueId;

    @NotNull(message = "cliente é obrigátorio")
    private Long clienteId;

    @NotNull(message = "quantidade é obrigátorio")
    @PositiveOrZero(message = "a quantidade precisa ser positivo ou zero")
    private Integer quantidade;

    @NotNull(message = "data de venda é obrigátorio")
    private LocalDate dataVenda;

}
