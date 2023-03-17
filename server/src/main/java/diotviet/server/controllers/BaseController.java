package diotviet.server.controllers;

import diotviet.server.templates.GeneralResponse;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {
    /**
     * OK response wrapper without message
     *
     * @param any
     * @return
     */
    protected ResponseEntity<GeneralResponse> ok (Object any) {
        return ResponseEntity.ok(GeneralResponse.success(any));
    }

    /**
     * OK response wrapper with message
     *
     * @param any
     * @return
     */
    protected ResponseEntity<GeneralResponse> ok (String message, Object any) {
        return ResponseEntity.ok(GeneralResponse.success(message, any));
    }
}
