package produto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProdutoRequest {

    @NotBlank
    private String nome;


    @NotNull()
    private long categoriaId;

    @NotBlank
    private String codigo;

    @Positive
    @NotNull
    private BigDecimal valor;

    @NotNull
    private Boolean ativo;
}
