package diotviet.server.controllers;

import diotviet.server.constants.Type;
import diotviet.server.entities.Category;
import diotviet.server.entities.Customer;
import diotviet.server.entities.Group;
import diotviet.server.services.CategoryService;
import diotviet.server.services.CustomerService;
import diotviet.server.services.GroupService;
import diotviet.server.templates.Customer.CustomerInitResponse;
import diotviet.server.templates.Customer.CustomerInteractRequest;
import diotviet.server.templates.Customer.CustomerSearchRequest;
import diotviet.server.templates.Customer.CustomerSearchResponse;
import diotviet.server.templates.EntityHeader;
import diotviet.server.utils.EntityUtils;
import diotviet.server.views.Customer.CustomerSearchView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping(value = "/api/v1/customer", produces = "application/json")
public class CustomerController extends BaseController {
    // ****************************
    // Properties
    // ****************************

    /**
     * Customer service
     */
    @Autowired
    private CustomerService customerService;
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
    public ResponseEntity<?> index(CustomerSearchRequest request) {
        // Get headers
        EntityHeader[] headers = entityUtils.getHeaders(Customer.class);
        // Get list of Customers (get all data, no need to filter anything)
        Page<CustomerSearchView> items = customerService.paginate(request);

         List<Category> categories = categoryService.getCategories(Type.PARTNER);
        // Get group list for FilterPanel
        List<Group> groups = groupService.getGroups(Type.PARTNER);

        return ok(new CustomerInitResponse(headers, items, categories, groups));
    }

    /**
     * Search for Customer that satisfy condition
     *
     * @param request
     * @return
     */
    @GetMapping("/search")
    public ResponseEntity<?> search(CustomerSearchRequest request) {
        // Search for data and response
        return ok(new CustomerSearchResponse(customerService.paginate(request)));
    }

    /**
     * Show detail
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        return ok(customerService.findById(id));
    }

    /**
     * Store (Create) item
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/store", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> store(CustomerInteractRequest request) {
        // Store item
        this.customerService.store(request);

        return ok("");
    }
//
//    /**
//     * Partial update item
//     *
//     * @param request
//     * @return
//     */
//    @PatchMapping(value = "/patch")
//    public ResponseEntity<?> patch(@RequestBody ProductPatchRequest request) {
//        // Store item
//        this.productService.patch(request);
//
//        return ok("");
//    }
//
//    /**
//     * Delete item
//     *
//     * @param ids
//     * @return
//     */
//    @DeleteMapping(value = "/delete")
//    public ResponseEntity<?> delete(@RequestParam("ids") Long[] ids) {
//        // Store item
//        this.productService.delete(ids);
//
//        return ok("");
//    }
//
//    /**
//     * Import CSV
//     *
//     * @param file
//     * @return
//     */
//    @PostMapping(value = "/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//    public ResponseEntity<?> importCSV(@RequestPart("file") MultipartFile file) {
//        // Parse CSV file
//        List<Product> products = parse(file, Product.class);
//        // Prep the importer
//        importService.prep();
//        // Re-attach (or pull) any relationship
//        importService.pull(products);
//        // Run import
//        importService.runImport(products);
//
//        return ok("");
//    }
//
//    @GetMapping(value = "/export")
//    public ResponseEntity<?> exportCSV() {
//        // Export Bean to CSV
//        byte[] bytes = export(productService.export());
//
//        return ResponseEntity.ok()
//                .header("Content-Disposition", "attachment; filename=ahihi.imports")
//                .contentLength(bytes.length)
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(new ByteArrayResource(bytes));
//    }
}
