package diotviet.server.entities;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.annotation.JsonIgnore;
import diotviet.server.constants.Role;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.WhereJoinTable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

/**
 * User model
 */
@Entity
@Table(name = "users")
@Data
@Accessors(chain = true)
public class User implements UserDetails {

    // ****************************
    // Properties
    // ****************************

    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @SequenceGenerator(name = "users_seq", sequenceName = "users_seq", allocationSize = 1)
    private long id;

    /**
     * Name
     */
    @Column(length = 30)
    private String name;
    /**
     * Email
     */
    @Column(length = 50, nullable = false)
    private String email;

    /**
     * Password
     */
    @Column(length = 100, nullable = false)
    @JsonIgnore
    private String password;

    /**
     * Role
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Role role;

    /**
     * Access tokens
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Collection<AccessToken> tokens;

    /**
     * Token that is valid (not expired yet)
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Where(clause = "expired_at > CURRENT_TIMESTAMP and is_deleted = false")
    private Collection<AccessToken> validTokens;

    @Transient
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private String activeToken;

    // ****************************
    // Public API
    // ****************************

    /**
     * Subscribe token
     *
     * @param token
     * @return
     */
    public AccessToken subscribeToken(DecodedJWT token) {
        // Create AccessToken instance
        AccessToken accessToken = new AccessToken(token);

        // Subscribe
        accessToken.subscribe(this);
        // Create token instance and add it into token list
        tokens.add(accessToken);

        return accessToken;
    }

    // ****************************
    // Overridden API
    // ****************************

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList(Arrays.stream(switch (role) {
            case ROLE_ADMIN -> new Role[]{Role.ROLE_ADMIN, Role.ROLE_SUPER, Role.ROLE_STAFF};
            case ROLE_SUPER -> new Role[]{Role.ROLE_SUPER, Role.ROLE_STAFF};
            case ROLE_STAFF -> new Role[]{Role.ROLE_STAFF};
            default -> new Role[]{Role.ROLE_GUEST};
        }).map(Role::toString).toArray(String[]::new));
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
