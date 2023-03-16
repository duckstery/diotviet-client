package diotviet.server.services;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import diotviet.server.entities.AccessToken;
import diotviet.server.entities.User;
import diotviet.server.repositories.UserRepository;
import diotviet.server.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserService implements UserDetailsService {

    // ****************************
    // Properties
    // ****************************

    @Autowired
    private UserRepository repository;
    /**
     * JWT Utility
     */
    @Autowired
    JWTUtils jwtUtils;

    // ****************************
    // Public API
    // ****************************

    /**
     * Check if user is existed
     *
     * @return
     */
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    /**
     * Save user
     *
     * @param user
     * @return
     */
    public User save(User user) {
        return repository.save(user);
    }

    /**
     * Issue token for user
     *
     * @param user
     * @return
     */
    public String issueToken(Authentication authentication) {
        // Get authenticated user
        User user = (User) authentication.getPrincipal();

        // Generate token
        String jwt = jwtUtils.generate(user);
        // Subscribe token
        user.subscribeToken(jwtUtils.decode(jwt));

        // Save
        repository.save(user);

        return jwt;
    }

    /**
     * Verify token
     *
     * @return
     */
    public User verifyToken(DecodedJWT jwt) {
        // Get claims
        Map<String, Claim> claims = jwt.getClaims();
        // Load user
        User user = (User) loadUserByUsername(claims.get("email").asString());

        // Check if token exists in user's valid tokens
        if (user.getValidTokens().stream().noneMatch(accessToken -> accessToken.match(jwt.getToken()))) {
            throw new TokenExpiredException("Token is expired", jwt.getExpiresAtAsInstant());
        }

        return user;
    }

    // ****************************
    // Private
    // ****************************

    // ****************************
    // Overridden
    // ****************************

    /**
     * Load user by email to be precise
     *
     * @param username the username identifying the user whose data is required.
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
    }
}
