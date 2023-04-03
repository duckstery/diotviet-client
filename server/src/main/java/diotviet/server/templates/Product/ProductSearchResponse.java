package diotviet.server.templates.Product;

import diotviet.server.entities.Product;
import org.springframework.data.domain.Page;

public record ProductSearchResponse(Page<Product> items) {
}
