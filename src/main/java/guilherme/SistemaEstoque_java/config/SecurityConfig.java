package guilherme.SistemaEstoque_java.config;


import guilherme.SistemaEstoque_java.jwt.FiltroJwt;
import guilherme.SistemaEstoque_java.jwt.RateLimitFilter;
import guilherme.SistemaEstoque_java.usuario.TipoUsuario;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ADMIN = TipoUsuario.ADMIM.name();
    private static final String USUARIO = TipoUsuario.USUARIO.name();

    private static final String[] MOVIMENTACOES = {"/compras/**", "/vendas/**"};
    private static final String[] CADASTRO = {"/fornecedores/**", "/transferencia/**", "/produtos/**"};

    private final FiltroJwt filtroJwt;
    private final RateLimitFilter rateLimitFilter;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()

                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/auth/login", "/auth/cadastrar", "/auth/refresh", "/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.GET, "/usuarios").hasRole(ADMIN)
                        .requestMatchers("/usuarios/**").hasAnyRole(USUARIO, ADMIN)

                        .requestMatchers(MOVIMENTACOES).hasAnyRole(USUARIO, ADMIN)
                        .requestMatchers(CADASTRO).hasAnyRole(USUARIO, ADMIN)
                        .anyRequest().denyAll())

                .addFilterBefore(filtroJwt, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(rateLimitFilter, FiltroJwt.class)
                .build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return  authenticationConfiguration.getAuthenticationManager();
    }
}
