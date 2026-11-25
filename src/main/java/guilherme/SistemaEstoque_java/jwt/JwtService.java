package guilherme.SistemaEstoque_java.jwt;



import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import guilherme.SistemaEstoque_java.usuario.Usuario;

import java.time.Instant;

@Service
public class JwtService {
    private static final String EMISSOR = "sistema=estoque";

    @Value("${jwt.secret}")
    private String secret;

    public String gerar(Usuario usuario) {
        return JWT.create()
                .withIssuer(EMISSOR)
                .withSubject(usuario.getEmail())
                .withExpiresAt(Instant.now().plusSeconds(900))
                .sign(Algorithm.HMAC256(secret));

    }

    public String validar(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer(EMISSOR)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }
}
