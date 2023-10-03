package diotviet.server.controllers;

import diotviet.server.entities.AccessToken;
import diotviet.server.entities.User;
import diotviet.server.templates.User.LoginRequest;
import diotviet.server.templates.User.SignupRequest;
import diotviet.server.services.UserService;
import diotviet.server.traits.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/api/auth", produces = "application/json")
public class UserController extends BaseController {

    // ****************************
    // Properties
    // ****************************

    /**
     * User service
     */
    @Autowired
    private UserService service;
    /**
     * Authentication manager
     */
    @Autowired
    AuthenticationManager authenticationManager;

    /**
     * Password encoder
     */
    @Autowired
    PasswordEncoder encoder;

    // ****************************
    // Public API
    // ****************************

    /**
     * Login
     *
     * @param request
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Authenticate user's credential
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        // Set authenticated information to context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT
        AccessToken token = service.issueToken(authentication);
        return ok(__("login_success"), token);
    }

    /**
     * Logout
     *
     * @return
     */
    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        // Unsubscribe current active token
        service.unsubscribeToken(SecurityContextHolder.getContext().getAuthentication());
        // Clear authentication
        SecurityContextHolder.getContext().setAuthentication(null);

        return ok(__("logout_success"), null);
    }

    /**
     * Login
     *
     * @param request
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignupRequest request) {
        // Check if user is existed
        if (service.existsByUsername(request.username())) {
            return ResponseEntity.badRequest().body("ahihi");
        }

        // Create new User
        User user = new User()
                .setName(request.name())
                .setUsername(request.username())
                .setPassword(encoder.encode(request.password()))
                .setRole(request.role());

        service.save(user);

        return ok(user);
    }
}
