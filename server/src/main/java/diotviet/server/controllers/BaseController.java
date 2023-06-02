package diotviet.server.controllers;

import diotviet.server.templates.GeneralResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {

    // ****************************
    // Properties
    // ****************************

    @Autowired
    private MessageSource messageSource;

    // ****************************
    // Public API
    // ****************************

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

    /**
     * Translate key with default message
     *
     * @param key
     * @param defaultMessage
     * @return
     */
    protected String __(String key, String defaultMessage) {
        return messageSource.getMessage(key, null, defaultMessage, LocaleContextHolder.getLocale());
    }

    /**
     * Translate key without default message
     *
     * @param key
     * @return
     */
    protected String __(String key) {
        return __(key, key);
    }

    /**
     *
     * @param key
     * @param args
     * @return
     */
    protected String __(String key, Object[] args) {
        return messageSource.getMessage(key, args, key, LocaleContextHolder.getLocale());
    }
}
