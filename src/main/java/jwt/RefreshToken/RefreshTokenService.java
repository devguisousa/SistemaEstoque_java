package jwt.RefreshToken;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usuario.Usuario;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private static final long DURACAO_EM_DIAS = 7;

    private final RefreshTokenRepository refreshRepository;
    private final SecureRandom random = new SecureRandom();

    public record Rotacao(Usuario usuario, String novoRefreshToken) {}

    @Transactional
    public String criar(Usuario usuario) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsuario(usuario);
        refreshToken.setHash(gerarHash(token));
        refreshToken.setExpiraEm(Instant.now().plus(DURACAO_EM_DIAS, ChronoUnit.DAYS));
        refreshRepository.save(refreshToken);

        return token;
    }

    @Transactional
    public Rotacao rotacionar(String token) {
        RefreshToken tokenAtual = refreshRepository.findByHash(gerarHash(token)).orElseThrow(()-> new RuntimeException("Rrefesh token invalido"));
        if (tokenAtual.isRevogado() || tokenAtual.getExpiraEm().isBefore(Instant.now())) {
            throw  new RuntimeException("Refresh token invalido");
        }

        tokenAtual.setRevogado(true);
        Usuario usuario = tokenAtual.getUsuario();
        usuario.getEmail();

        return new Rotacao(usuario, criar(usuario));


    }

    @Transactional
    public  void revogar(String token) {
        refreshRepository.findByHash(gerarHash(token)).ifPresent(refreshToken -> refreshToken.setRevogado(true));

    }

    private String gerarHash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw  new IllegalStateException(e);
        }
    }



}
