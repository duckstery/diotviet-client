package diotviet.server.controllers;

import diotviet.server.services.TransactionService;
import diotviet.server.templates.Report.DetailReportRequest;
import diotviet.server.templates.Transaction.TransactionInteractRequest;
import diotviet.server.traits.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/api/v1/transaction", produces = "application/json")
public class TransactionController extends BaseController {
    // ****************************
    // Properties
    // ****************************

    /**
     * Transaction service
     */
    @Autowired
    private TransactionService service;

    // ****************************
    // Public API
    // ****************************

    /**
     * Store (Create) transaction
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/store", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> store(TransactionInteractRequest request) {
        // Store item
        service.store(request);

        return ok("");
    }

    /**
     * Report income
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/report")
    public ResponseEntity<?> report(DetailReportRequest request) {
        return ok(service.report(request));
    }
}
