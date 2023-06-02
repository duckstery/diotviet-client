package diotviet.server.templates.Product;

import diotviet.server.entities.Product;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

/**
 * Product params
 */
@Getter
public class ProductParam extends Product {
    private MultipartFile file;
}
