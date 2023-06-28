package diotviet.server.templates.Group;

import diotviet.server.constants.Type;

/**
 * Group interact request
 *
 * @param id
 * @param name
 * @param type
 */
public record GroupInteractRequest(
        Long id,
        String name,
        Type type
) {
}
