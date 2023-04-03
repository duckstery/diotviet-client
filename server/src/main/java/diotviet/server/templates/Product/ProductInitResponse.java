package diotviet.server.templates.Product;

import diotviet.server.entities.Category;
import diotviet.server.entities.Group;
import diotviet.server.entities.Product;
import diotviet.server.templates.EntityHeader;
import org.springframework.data.domain.Page;

import java.util.List;

public record ProductInitResponse(
        EntityHeader[] headers,
        Page<Product> items,
        List<Category> categories,
        List<Group> groups) {
}
