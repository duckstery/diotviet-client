package diotviet.server.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/api/fallback", produces="application/json")
public class FallbackController extends BaseController {
    @GetMapping("/")
    public String index() {

        return "Greetings from Spring Boot!";
    }

    @GetMapping("/ping")
    @PreAuthorize("hasRole('STAFF')")
    public String ping() throws Exception {
        throw new Exception("ahihi");
    }
}
