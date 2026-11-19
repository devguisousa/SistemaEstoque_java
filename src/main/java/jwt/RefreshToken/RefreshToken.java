package jwt.RefreshToken;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import usuario.Usuario;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, unique = true, updatable = false)
    private String hash;

    @Column(nullable = false, updatable = false)
    private Instant expiraEm;


    @Column(nullable = false)
    private boolean revogado = true;
}
