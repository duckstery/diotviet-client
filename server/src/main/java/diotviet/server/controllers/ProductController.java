package diotviet.server.controllers;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import diotviet.server.constants.Type;
import diotviet.server.entities.Category;
import diotviet.server.entities.Group;
import diotviet.server.entities.Product;
import diotviet.server.exceptions.FileUploadingException;
import diotviet.server.services.CategoryService;
import diotviet.server.services.GroupService;
import diotviet.server.services.ProductService;
import diotviet.server.templates.EntityHeader;
import diotviet.server.templates.Product.*;
import diotviet.server.utils.EntityUtils;
import diotviet.server.views.Product.ProductSearchView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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

    /**
     * Partial update item
     *
     * @param request
     * @return
     */
    @PatchMapping(value = "/patch")
    public ResponseEntity<?> patch(@RequestBody ProductPatchRequest request) {
        // Store item
        this.productService.patch(request);

        return ok("");
    }

    /**
     * Delete item
     *
     * @param ids
     * @return
     */
    @DeleteMapping(value = "/delete")
    public ResponseEntity<?> delete(@RequestParam("ids") Long[] ids) {
        // Store item
        this.productService.delete(ids);

        return ok("");
    }

    @PostMapping(value = "/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> importCSV(@RequestPart("file") MultipartFile file) {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CsvToBean<Product> csvToBean = new CsvToBeanBuilder<Product>(reader)
                    .withType(Product.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build();

            List<Product> products = csvToBean.parse();
        } catch (IOException ignored) {
            throw new FileUploadingException();
        }

        return ok("");
    }
}
