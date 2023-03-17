package diotviet.server.templates;

/**
 * Login request format
 *
 * @param email
 * @param password
 */
public record LoginRequest(String email, String password) {
}
