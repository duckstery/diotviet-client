package diotviet.server.controllers;

import diotviet.server.entities.QProduct;
import diotviet.server.templates.GeneralResponse;
import diotviet.server.utils.EntityUtils;
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

    @GetMapping("/")
    public String index() {

        return "Greetings from Spring Boot!";
    }

    @GetMapping("/ping")
    public ResponseEntity<GeneralResponse> ping() throws Exception {
        System.out.println(QProduct.product.getClass());
        return ok(null);
    }
}
