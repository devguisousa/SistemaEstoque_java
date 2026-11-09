package produto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AtualizacaoRequest {

    @NotBlank(message = "NÃO PODER SER VAZIO")
    private String nome;

    @NotNull(message = "NÃO PODER SER NULO")
    @PositiveOrZero(message = "TEM QUE SER IGUAL OU MAIOR QUE 0")
    private BigDecimal valor;

    @NotNull(message = "NÃO PODER SER NULA")
    private Long categoriaId;
}
