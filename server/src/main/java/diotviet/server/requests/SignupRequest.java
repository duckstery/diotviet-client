package diotviet.server.requests;

import diotviet.server.constants.Role;

public record SignupRequest(String name, String email, String password, Role role) {
}
