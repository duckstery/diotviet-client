package diotviet.server.templates.Product;

import diotviet.server.entities.Category;
import diotviet.server.entities.Group;
import diotviet.server.entities.Product;
import diotviet.server.templates.EntityHeader;

import java.util.List;

public record ProductInitResponse(
        EntityHeader[] headers,
        Iterable<Product> items,
        List<Category> categories,
        List<Group> groups) {
}
