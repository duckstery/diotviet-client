package diotviet.server.entities;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "access_tokens")
@Getter
@NoArgsConstructor
public class AccessToken {

    // ****************************
    // Properties
    // ****************************

    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "access_tokens_seq")
    @SequenceGenerator(name = "access_tokens_seq", sequenceName = "access_tokens_seq", allocationSize = 1)
    private long id;
    /**
     * Value
     */
    @Column(length = 300, nullable = false)
    private String value;
    /**
     * Issue at
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date issuedAt;
    /**
     * Expire at
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiredAt;
    /**
     * Corresponding User
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;

    // ****************************
    // Constructors
    // ****************************

    /**
     * Constructors
     *
     * @param jwt
     */
    public AccessToken(DecodedJWT jwt) {
        this.value = jwt.getToken();
        this.issuedAt = jwt.getIssuedAt();
        this.expiredAt = jwt.getExpiresAt();
    }

    /**
     * Subscribe User
     */
    public void subscribe(User user) {
        if (Objects.isNull(this.user)) {
            this.user = user;
        }
    }

    /**
     * Check if this token's value match value
     *
     * @param value
     * @return
     */
    public boolean match(String value) {
        return this.value.equals(value);
    }
}
