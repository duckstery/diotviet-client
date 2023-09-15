package diotviet.server.controllers;

import diotviet.server.services.TransactionService;
import diotviet.server.templates.Report.DetailReportRequest;
import diotviet.server.traits.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
