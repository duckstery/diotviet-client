package diotviet.server.controllers;

import diotviet.server.entities.Customer;
import diotviet.server.entities.QProduct;
import diotviet.server.repositories.CustomerRepository;
import diotviet.server.repositories.ProductRepository;
import diotviet.server.templates.GeneralResponse;
import diotviet.server.traits.BaseController;
import diotviet.server.utils.EntityUtils;
import diotviet.server.utils.StorageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/api/fallback", produces="application/json")
public class FallbackController extends BaseController {

    @Autowired
    private EntityUtils entityUtils;

    @Autowired
    private CustomerRepository repository;

    @GetMapping("")
    public String index() {
//        StorageUtils.delete("3NKgDjW");
        return "Greetings from Spring Boot!";
    }

    @GetMapping("/ping")
    public ResponseEntity<GeneralResponse> ping() throws Exception {
        System.out.println(QProduct.product.getClass());
        return ok(null);
    }
}
