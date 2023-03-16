package diotviet.server.requests;

/**
 * Login request format
 *
 * @param email
 * @param password
 */
public record LoginRequest(String email, String password) {
}
