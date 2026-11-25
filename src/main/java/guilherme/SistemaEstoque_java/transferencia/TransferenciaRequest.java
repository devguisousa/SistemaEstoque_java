package guilherme.SistemaEstoque_java.transferencia;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TransferenciaRequest {

    @NotNull(message = "O estoque de origem é obrigatório")
    private Long estoqueOrigemId;

    @NotNull(message = "O estoque de destino é obrigatório")
    private Long estoqueDestinoId;

    @NotNull(message = "A quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser maior que zero")
    private Integer quantidade;

    @NotNull(message = "A data da transferência é obrigatória")
    @PastOrPresent(message = "A data da transferência não pode ser futura")
    private LocalDate dataTransferencia;
}