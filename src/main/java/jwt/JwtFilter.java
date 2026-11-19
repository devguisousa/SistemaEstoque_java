package jwt;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import usuario.UsuarioRepository;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = extrairToken(request);

        if (token != null) {
            String email = jwtService.validar(token);

            if (email != null) {
                usuarioRepository.findByEmail(email).ifPresent(usuario -> {
                    var autenticacao = new UsernamePasswordAuthenticationToken(
                            usuario, null, usuario.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(autenticacao);
                });
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extrairToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7);
    }
}
