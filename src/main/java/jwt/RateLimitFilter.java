package jwt;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Set<String> ROTAS_LIMITADAS =
            Set.of("/auth/login", "/auth/cadastrar", "/auth/refresh");

    private final ConcurrentHashMap<String, Bucket> baldes = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"POST".equals(request.getMethod())
                || !ROTAS_LIMITADAS.contains(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String chave = request.getRemoteAddr() + ":" + request.getRequestURI();
        Bucket balde = baldes.computeIfAbsent(chave, k -> novoBalde());

        ConsumptionProbe resultado = balde.tryConsumeAndReturnRemaining(1);

        if (resultado.isConsumed()) {
            filterChain.doFilter(request, response);
            return;
        }

        long segundos = Math.max(1, resultado.getNanosToWaitForRefill() / 1_000_000_000);
        response.setStatus(429);
        response.setHeader("Retry-After", String.valueOf(segundos));
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                "{\"status\":429,\"detail\":\"Muitas tentativas. Tente novamente em " + segundos + " segundos.\"}");
    }

    private Bucket novoBalde() {
        return Bucket.builder()
                .addLimit(limite -> limite.capacity(5).refillIntervally(5, Duration.ofMinutes(1)))
                .build();
    }
}