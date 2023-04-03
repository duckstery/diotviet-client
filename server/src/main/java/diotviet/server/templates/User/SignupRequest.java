package diotviet.server.templates.User;

import diotviet.server.constants.Role;

public record SignupRequest(String name, String email, String password, Role role) {
}
