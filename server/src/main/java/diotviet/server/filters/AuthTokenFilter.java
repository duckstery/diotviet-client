package diotviet.server.filters;

import diotviet.server.entities.User;
import diotviet.server.services.UserService;
import diotviet.server.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

public class AuthTokenFilter extends OncePerRequestFilter {

    // ****************************
    // Properties
    // ****************************

    /**
     * JWT Utility
     */
    @Autowired
    private JWTUtils jwtUtils;
    /**
     * User Service (AKA UserDetailsService)
     */
    @Autowired
    private UserService userService;
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    // ****************************
    // Overridden API
    // ****************************

    /**
     * Filter logic<br>
     * Verify JWT. If valid, create AuthenticationToken with user data
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // Get JWT in request "Authorization" header
            String token = getToken(request);
            // Check if token is not null
            if (Objects.nonNull(token)) {
                // Verify and get user ID
                jwtUtils.verify(token);
                // Verify and load  user
                User user = userService.verifyToken(jwtUtils.decode(token));

                // Authenticate
                authenticate(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    // ****************************
    // Private
    // ****************************

    /**
     * Get JWT in "Authorization" header
     *
     * @param request
     * @return
     */
    private String getToken(HttpServletRequest request) {
        // Get "Authorization" header
        String authHeader = request.getHeader("Authorization");

        // Check if header has text and contains "Bearer "
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    /**
     * Authenticate user
     *
     * @param user
     * @param role
     */
    private void authenticate(User user) {
        // Create AuthenticationToken
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        // Set AuthenticationToken to SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
