package guilherme.SistemaEstoque_java.compra;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CompraRequest {

    @NotNull(message = "O estoque é obrigatório")
    private Long estoqueId;

    @NotNull(message = "O fornecedor é obrigatório")
    private Long fornecedorId;

    @NotNull(message = "A quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser maior que zero")
    private Integer quantidade;

    @NotNull(message = "A data da compra é obrigatória")
    @PastOrPresent(message = "A data da compra não pode ser futura")
    private LocalDate dataCompra;



    private LocalDate dataRecebimento;

}