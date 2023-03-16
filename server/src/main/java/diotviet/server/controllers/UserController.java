package diotviet.server.controllers;

import diotviet.server.entities.User;
import diotviet.server.requests.LoginRequest;
import diotviet.server.requests.SignupRequest;
import diotviet.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Authenticate user's credential
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        // Set authenticated information to context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT
        String token = service.issueToken(authentication);

        return ResponseEntity.ok(token);
    }

    /**
     * Login
     *
     * @param request
     * @return
     */
    @GetMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignupRequest request) {
        // Check if user is existed
        if (service.existsByEmail(request.email())) {
            return ResponseEntity.badRequest().body("ahihi");
        }

        // Create new User
        User user = new User()
                .setName(request.name())
                .setEmail(request.email())
                .setPassword(encoder.encode(request.password()))
                .setRole(request.role());

        service.save(user);

        return ResponseEntity.ok("Okie");
    }
}
