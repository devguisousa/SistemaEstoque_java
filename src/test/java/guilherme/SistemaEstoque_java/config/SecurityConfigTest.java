package guilherme.SistemaEstoque_java.config;

import guilherme.SistemaEstoque_java.auth.AuthService;
import guilherme.SistemaEstoque_java.categoria.CategoriaService;
import guilherme.SistemaEstoque_java.compra.CompraService;
import guilherme.SistemaEstoque_java.estoque.EstoqueService;
import guilherme.SistemaEstoque_java.fornecedor.FornecedorService;
import guilherme.SistemaEstoque_java.jwt.JwtService;
import guilherme.SistemaEstoque_java.produto.ProdutoService;
import guilherme.SistemaEstoque_java.usuario.TipoUsuario;
import guilherme.SistemaEstoque_java.usuario.Usuario;
import guilherme.SistemaEstoque_java.usuario.UsuarioRepository;
import guilherme.SistemaEstoque_java.venda.VendaService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Garante a matriz de acesso definida em {@link SecurityConfig}.
 * Os serviços são mocks: aqui só importa se a requisição passa pela segurança.
 */
@WebMvcTest
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired MockMvc mvc;

    @MockitoBean AuthService authService;
    @MockitoBean CategoriaService categoriaService;
    @MockitoBean CompraService compraService;
    @MockitoBean EstoqueService estoqueService;
    @MockitoBean FornecedorService fornecedorService;
    @MockitoBean ProdutoService produtoService;
    @MockitoBean VendaService vendaService;
    @MockitoBean JwtService jwtService;
    @MockitoBean UsuarioRepository usuarioRepository;

    private static final Usuario ADMIN = usuario(1L, TipoUsuario.ADMIM);
    private static final Usuario COMUM = usuario(2L, TipoUsuario.USUARIO);

    /**
     * Passou pela segurança e a rota existe (o status final depende do controller/validação).
     * 404/405 também falham, para pegar rota digitada errada aqui ou no controller.
     */
    private static final ResultMatcher LIBERADO =
            r -> assertThat(r.getResponse().getStatus()).isNotIn(401, 403, 404, 405);

    private static Usuario usuario(Long id, TipoUsuario tipo) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setEmail(tipo.name().toLowerCase() + "@teste.com");
        u.setNome(tipo.name());
        u.setSenha("x");
        u.setTipoUsuario(tipo);
        return u;
    }

    private static MockHttpServletRequestBuilder como(Usuario u, MockHttpServletRequestBuilder req) {
        return req.with(authentication(new UsernamePasswordAuthenticationToken(u, null, u.getAuthorities())));
    }

    private static MockHttpServletRequestBuilder req(String metodo, String rota) {
        return request(HttpMethod.valueOf(metodo), rota)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");
    }

    @Nested
    class Visitante {

        @ParameterizedTest
        @CsvSource({"/auth/login", "/auth/cadastrar", "/auth/refresh", "/auth/logout"})
        void podeUsarRotasDeAutenticacao(String rota) throws Exception {
            mvc.perform(req("POST", rota)).andExpect(LIBERADO);
        }

        @Test
        void podeAbrirDocumentacao() throws Exception {
            // o springdoc não sobe no @WebMvcTest (dá 404); aqui só importa não ser barrado
            mvc.perform(get("/v3/api-docs"))
                    .andExpect(r -> assertThat(r.getResponse().getStatus()).isNotIn(401, 403));
        }

        @ParameterizedTest
        @CsvSource({
                "GET, /produtos/listar",
                "GET, /Categoria/listar",
                "GET, /fornecedores/listar",
                "GET, /estoque/listar",
                "GET, /compras/listar",
                "GET, /vendas/listar",
                "POST, /vendas/cadastrar",
                "GET, /usuarios",
                "GET, /usuarios/1",
                "GET, /rota-inexistente"
        })
        void recebe401NoResto(String metodo, String rota) throws Exception {
            mvc.perform(req(metodo, rota)).andExpect(status().isUnauthorized());
        }

        @Test
        void tokenInvalidoRecebe401() throws Exception {
            when(jwtService.validar("invalido")).thenReturn(null);

            mvc.perform(get("/produtos/listar").header("Authorization", "Bearer invalido"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void loginTemLimiteDeTentativas() throws Exception {
            for (int i = 0; i < 5; i++) {
                mvc.perform(req("POST", "/auth/login").with(ip("10.9.9.9"))).andExpect(LIBERADO);
            }
            mvc.perform(req("POST", "/auth/login").with(ip("10.9.9.9")))
                    .andExpect(status().isTooManyRequests());
        }

        private static org.springframework.test.web.servlet.request.RequestPostProcessor ip(String ip) {
            return r -> {
                r.setRemoteAddr(ip);
                return r;
            };
        }
    }

    @Nested
    class UsuarioComum {

        @ParameterizedTest
        @CsvSource({
                // leitura de tudo
                "GET, /produtos/listar",
                "GET, /produtos/buscar/1",
                "GET, /Categoria/listar",
                "GET, /fornecedores/listar",
                "GET, /fornecedores/cliente/1",
                "GET, /estoque/buscar/1",
                "GET, /compras/listar",
                "GET, /vendas/buscar/1",
                // operação de compras e vendas
                "POST, /compras/cadastrar",
                "PUT, /compras/atualizar/1",
                "PATCH, /compras/comprar/1",
                "PATCH, /compras/cancelar/1",
                "POST, /vendas/cadastrar",
                "PUT, /vendas/atualizar/1",
                "PATCH, /vendas/vender/1",
                "PATCH, /vendas/cancelar/1"
        })
        void podeConsultarEOperar(String metodo, String rota) throws Exception {
            mvc.perform(como(COMUM, req(metodo, rota))).andExpect(LIBERADO);
        }

        @ParameterizedTest
        @CsvSource({
                "POST, /produtos/cadastro",
                "PUT, /produtos/atualizar/1",
                "PATCH, /produtos/desativar/1",
                "POST, /Categoria/cadastrar",
                "DELETE, /Categoria/deletar/1",
                "POST, /fornecedores/cadastrar",
                "PATCH, /fornecedores/ativar/1",
                "POST, /estoque/cadastrar",
                "DELETE, /estoque/deletar/1",
                "GET, /usuarios",
                "GET, /rota-inexistente"
        })
        void naoPodeAlterarCadastrosBase(String metodo, String rota) throws Exception {
            mvc.perform(como(COMUM, req(metodo, rota))).andExpect(status().isForbidden());
        }

        @Test
        void veEAtualizaApenasAPropriaConta() throws Exception {
            mvc.perform(como(COMUM, get("/usuarios/2"))).andExpect(status().isOk());
            mvc.perform(como(COMUM, atualizarUsuario(2))).andExpect(status().isOk());

            mvc.perform(como(COMUM, get("/usuarios/1"))).andExpect(status().isForbidden());
            mvc.perform(como(COMUM, atualizarUsuario(1))).andExpect(status().isForbidden());
        }

        @Test
        void tokenValidoAutentica() throws Exception {
            when(jwtService.validar("valido")).thenReturn(COMUM.getEmail());
            when(usuarioRepository.findByEmail(COMUM.getEmail())).thenReturn(Optional.of(COMUM));

            mvc.perform(get("/produtos/listar").header("Authorization", "Bearer valido"))
                    .andExpect(status().isOk());
            mvc.perform(post("/produtos/cadastro").header("Authorization", "Bearer valido")
                            .contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class Admin {

        @ParameterizedTest
        @CsvSource({
                "POST, /produtos/cadastro",
                "PUT, /produtos/atualizar/1",
                "PATCH, /produtos/ativar/1",
                "POST, /Categoria/cadastrar",
                "PUT, /Categoria/atualizar/1",
                "DELETE, /Categoria/deletar/1",
                "POST, /fornecedores/cadastrar",
                "GET, /fornecedores/fornecedor/1",
                "PATCH, /fornecedores/desativar/1",
                "POST, /estoque/cadastrar",
                "DELETE, /estoque/deletar/1",
                "POST, /compras/cadastrar",
                "PATCH, /vendas/vender/1",
                "GET, /usuarios"
        })
        void podeTudo(String metodo, String rota) throws Exception {
            mvc.perform(como(ADMIN, req(metodo, rota))).andExpect(LIBERADO);
        }

        @Test
        void gerenciaQualquerConta() throws Exception {
            mvc.perform(como(ADMIN, get("/usuarios/2"))).andExpect(status().isOk());
            mvc.perform(como(ADMIN, atualizarUsuario(2))).andExpect(status().isOk());
        }

        @Test
        void rotaNaoMapeadaContinuaNegada() throws Exception {
            mvc.perform(como(ADMIN, get("/rota-inexistente"))).andExpect(status().isForbidden());
        }
    }

    private static MockHttpServletRequestBuilder atualizarUsuario(long id) {
        return put("/usuarios/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"novo@teste.com\"}");
    }
}
