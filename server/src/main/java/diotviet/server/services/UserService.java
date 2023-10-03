package diotviet.server.services;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import diotviet.server.constants.Role;
import diotviet.server.entities.AccessToken;
import diotviet.server.entities.User;
import diotviet.server.repositories.AccessTokenRepository;
import diotviet.server.repositories.UserRepository;
import diotviet.server.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService implements UserDetailsService {

    // ****************************
    // Properties
    // ****************************

    /**
     * User repository
     */
    @Autowired
    private UserRepository repository;
    /**
     * Access token repository
     */
    @Autowired
    private AccessTokenRepository accessTokenRepository;

    /**
     * JWT Utility
     */
    @Autowired
    JWTUtils jwtUtils;
    /**
     * I18N
     */
    @Autowired
    private MessageSource messageSource;

    /**
     * Password encoder
     */
    PasswordEncoder encoder;

    // ****************************
    // Public API
    // ****************************

    /**
     * Get requester name
     *
     * @return
     */
    public static String getRequester() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getName();
    }

    /**
     * Set password encoder
     *
     * @param passwordEncoder
     */
    public PasswordEncoder setPasswordEncoder(PasswordEncoder encoder) {
        return (this.encoder = encoder);
    }

    /**
     * Check if user is existed
     *
     * @return
     */
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
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
     * @param authentication
     * @return
     */
    public AccessToken issueToken(Authentication authentication) {
        // Get authenticated user
        User user = (User) authentication.getPrincipal();

        // Generate token
        String jwt = jwtUtils.generate(user);
        // Subscribe token
        AccessToken token = user.subscribeToken(jwtUtils.decode(jwt));

        // Save
        repository.save(user);

        return token;
    }

    /**
     * Unsubscribe token (unauthenticated)
     *
     * @param authentication
     */
    public void unsubscribeToken(Authentication authentication) {
        // Get authenticated user
        User user = (User) authentication.getPrincipal();
        // Delete current active token
        accessTokenRepository.deleteAccessTokenByToken(user.getActiveToken());
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
        User user = (User) loadUserByUsername(claims.get("username").asString());

        // Check if token exists in user's valid tokens
        if (user.getValidTokens().stream().noneMatch(accessToken -> accessToken.match(jwt.getToken()))) {
            // Get message
            String message = messageSource.getMessage("token_expired", null, LocaleContextHolder.getLocale());

            throw new TokenExpiredException(message, jwt.getExpiresAtAsInstant());
        }

        // Set active token
        user.setActiveToken(jwt.getToken());

        return user;
    }

    /**
     * Register an User
     *
     * @param username
     * @return
     */
    public User register(String name, String username, Role role) {

        return new User()
                .setName(name)
                .setUsername(username)
                .setPassword(encoder.encode("123456"))
                .setRole(role);
    }

    // ****************************
    // Private
    // ****************************

    // ****************************
    // Overridden
    // ****************************

    /**
     * Load user by username
     *
     * @param username the username identifying the user whose data is required.
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
    }
}
