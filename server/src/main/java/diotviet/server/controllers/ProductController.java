package diotviet.server.controllers;

import diotviet.server.constants.Type;
import diotviet.server.entities.Category;
import diotviet.server.entities.Group;
import diotviet.server.entities.Product;
import diotviet.server.services.CategoryService;
import diotviet.server.services.GroupService;
import diotviet.server.services.ProductService;
import diotviet.server.templates.EntityHeader;
import diotviet.server.templates.Product.ProductInitResponse;
import diotviet.server.templates.Product.ProductInteractRequest;
import diotviet.server.templates.Product.ProductSearchRequest;
import diotviet.server.templates.Product.ProductSearchResponse;
import diotviet.server.utils.EntityUtils;
import diotviet.server.views.Product.ProductSearchView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping(value = "/api/v1/product", produces = "application/json")
public class ProductController extends BaseController {
    // ****************************
    // Properties
    // ****************************

    /**
     * Product service
     */
    @Autowired
    private ProductService productService;
    /**
     * Category service
     */
    @Autowired
    private CategoryService categoryService;
    /**
     * Group service
     */
    @Autowired
    private GroupService groupService;
    /**
     * Utilities for Entity interact
     */
    @Autowired
    private EntityUtils entityUtils;

    // ****************************
    // Public API
    // ****************************

    /**
     * Index page
     *
     * @return
     */
    @GetMapping("/index")
    public ResponseEntity<?> index(ProductSearchRequest request) {
        // Get headers
        EntityHeader[] headers = entityUtils.getHeaders(Product.class);
        // Get list of Products (get all data, no need to filter anything)
        Page<ProductSearchView> items = productService.paginate(request);

        List<Category> categories = categoryService.getCategories(Type.PRODUCT);
        // Get group list for FilterPanel
        List<Group> groups = groupService.getGroups(Type.PRODUCT);

        return ok(new ProductInitResponse(headers, items, categories, groups));
    }

    /**
     * Search for Product that satisfy condition
     *
     * @param request
     * @return
     */
    @GetMapping("/search")
    public ResponseEntity<?> search(ProductSearchRequest request) {
        // Search for data and response
        return ok(new ProductSearchResponse(productService.paginate(request)));
    }

    /**
     * Show detail
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        return ok(productService.findById(id));
    }

    /**
     * Store (Create) item
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/store", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> store(ProductInteractRequest request) {
        // Store item
        this.productService.store(request);

        return ok("");
    }
}
