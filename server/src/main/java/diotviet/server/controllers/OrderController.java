package diotviet.server.controllers;

import com.google.zxing.WriterException;
import diotviet.server.constants.Status;
import diotviet.server.constants.Type;
import diotviet.server.entities.Group;
import diotviet.server.entities.Order;
import diotviet.server.exceptions.FileServingException;
import diotviet.server.services.DocumentService;
import diotviet.server.services.GroupService;
import diotviet.server.services.OrderService;
import diotviet.server.templates.Document.PrintableTag;
import diotviet.server.templates.EntityHeader;
import diotviet.server.templates.InitPrintResponse;
import diotviet.server.templates.Order.*;
import diotviet.server.templates.Product.ProductDisplayResponse;
import diotviet.server.traits.BaseController;
import diotviet.server.utils.EntityUtils;
import diotviet.server.utils.OtherUtils;
import diotviet.server.utils.PrintUtils;
import diotviet.server.views.Order.OrderSearchView;
import diotviet.server.views.Print.Order.OrderOrderPrintView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

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
    /**
     * Document service
     */
    @Autowired
    private DocumentService documentService;
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
    /**
     * Utilities for Print
     */
    @Autowired
    private PrintUtils printUtils;

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
     * Display items for page
     *
     * @return
     */
    @GetMapping("/init/print")
    public ResponseEntity<?> initPrint() {
        // Get active Document content
        String content = documentService.getActiveDocumentOfGroup("print_order").getContent();
        // Get PrintableTag
        PrintableTag[] tags = printUtils.getPrintableTag(OrderOrderPrintView.class);

        // Return data
        return ok(new InitPrintResponse(content, tags));
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
        Long id = orderService.store(request, Status.PENDING);

        return ok(id);
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
        Long id = orderService.store(request, Status.RESOLVED);

        return ok(id);
    }

    /**
     * Partial update item
     *
     * @param request
     * @return
     */
    @PatchMapping(value = "/patch")
    public ResponseEntity<?> patch(@RequestBody OrderPatchRequest request) {
        // Store item
        orderService.patch(request);

        return ok("");
    }

    /**
     * Simple search
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/query")
    public ResponseEntity<?> simpleSearch(OrderSearchRequest request) {
        return ok(orderService.query(request));
    }

    /**
     * Serve Barcode image
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/barcode/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<BufferedImage> genBarcode(@PathVariable Long id) {
        // Get Order
        String code = orderService.findCodeById(id);
        if (Objects.isNull(code)) {
            throw new FileServingException("file_not_exist");
        }
        // Generate Barcode
        BufferedImage bufferedImage = OtherUtils.generateBarcode(code);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(bufferedImage);
    }

    /**
     * Serve Barcode image
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/qrcode/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<BufferedImage> genQRCode(@PathVariable Long id) throws WriterException {
        // Get Order
        String code = orderService.findCodeById(id);
        if (Objects.isNull(code)) {
            throw new FileServingException("file_not_exist");
        }
        // Generate Barcode
        BufferedImage bufferedImage = OtherUtils.generateQRCode(code);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(bufferedImage);
    }

    /**
     * Get print data
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/print/{id}")
    public ResponseEntity<?> print(@PathVariable Long id) {
        return ok(orderService.print(id));
    }

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
