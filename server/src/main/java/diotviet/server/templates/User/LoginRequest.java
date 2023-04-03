package diotviet.server.templates.User;

/**
 * Login request format
 *
 * @param email
 * @param password
 */
public record LoginRequest(String email, String password) {
}
