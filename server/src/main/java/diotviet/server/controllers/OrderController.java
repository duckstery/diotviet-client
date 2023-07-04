package diotviet.server.controllers;

import diotviet.server.constants.Status;
import diotviet.server.constants.Type;
import diotviet.server.entities.Group;
import diotviet.server.entities.Order;
import diotviet.server.services.GroupService;
import diotviet.server.services.OrderService;
import diotviet.server.templates.EntityHeader;
import diotviet.server.templates.Order.OrderInitResponse;
import diotviet.server.templates.Order.OrderInteractRequest;
import diotviet.server.templates.Order.OrderSearchRequest;
import diotviet.server.templates.Order.OrderSearchResponse;
import diotviet.server.utils.EntityUtils;
import diotviet.server.views.Order.OrderSearchView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/api/v1/order", produces = "application/json")
public class OrderController extends BaseController {
    // ****************************
    // Properties
    // ****************************

    /**
     * Order service
     */
    @Autowired
    private OrderService orderService;
    /**
     * Group service
     */
    @Autowired
    private GroupService groupService;
//    /**
//     * Order import service
//     */
//    @Autowired
//    private OrderImportService importService;
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
    public ResponseEntity<?> index(OrderSearchRequest request) {
        // Get headers
        EntityHeader[] headers = entityUtils.getHeaders(Order.class);
        // Get list of Orders (get all data, no need to filter anything)
        Page<OrderSearchView> items = orderService.paginate(request);
        
        // Get group list for FilterPanel
        List<Group> groups = groupService.getGroups(Type.TRANSACTION);

        return ok(new OrderInitResponse(headers, items, groups));
    }

    /**
     * Search for Order that satisfy condition
     *
     * @param request
     * @return
     */
    @GetMapping("/search")
    public ResponseEntity<?> search(OrderSearchRequest request) {
        // Search for data and response
        return ok(new OrderSearchResponse(orderService.paginate(request)));
    }

    /**
     * Show detail
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        return ok(orderService.findById(id));
    }

    /**
     * Store (Create) item
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/order")
    public ResponseEntity<?> order(@RequestBody OrderInteractRequest request) {
        // Store item
        orderService.store(request, Status.PENDING);

        return ok("");
    }

    /**
     * Store (Create) item
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/purchase")
    public ResponseEntity<?> purchase(@RequestBody OrderInteractRequest request) {
        // Store item
        orderService.store(request, Status.RESOLVED);

        return ok("");
    }
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
//        orderService.delete(ids);
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
//        List<Order> orders = parse(file, Order.class);
//        // Prep the importer
//        importService.prep();
//        // Re-attach (or pull) any relationship
//        importService.pull(orders);
//        // Run import
//        importService.runImport(orders);
//
//        return ok("");
//    }
//
//    @GetMapping(value = "/export")
//    public ResponseEntity<?> exportCSV() {
//        // Export Bean to CSV
//        byte[] bytes = export(orderService.export());
//
//        return ResponseEntity.ok()
//                .header("Content-Disposition", "attachment; filename=ahihi.imports")
//                .contentLength(bytes.length)
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(new ByteArrayResource(bytes));
//    }
}
