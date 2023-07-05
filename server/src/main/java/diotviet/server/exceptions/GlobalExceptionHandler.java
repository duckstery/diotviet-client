package diotviet.server.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import diotviet.server.templates.GeneralResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.Arrays;

@ControllerAdvice
public class GlobalExceptionHandler {

    // ****************************
    // Properties
    // ****************************

    /**
     * I18N
     */
    @Autowired
    private MessageSource messageSource;
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    // ****************************
    // Handler
    // ****************************

    /**
     * Global exception handler
     *
     * @param request
     * @param response
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleGeneralError(HttpServletRequest request, HttpServletResponse response, Exception ex) throws IOException {
        // Create body
        GeneralResponse responseBody = new GeneralResponse(false, ex.getMessage(), ex.getClass());
        ex.printStackTrace();

        // Common handle logic
        commonLog(ex, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, responseBody);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleBadCredentials(HttpServletRequest request, HttpServletResponse response, Exception ex) throws IOException {
        // Create body
        GeneralResponse responseBody = new GeneralResponse(false, __("bad_credentials"), ex.getClass());
        // Common handle logic
        commonLog(ex, response, HttpServletResponse.SC_BAD_REQUEST, responseBody);
    }

    @ExceptionHandler(ServiceValidationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public void handleServiceValidation(HttpServletRequest request, HttpServletResponse response, ServiceValidationException ex) throws IOException {
        // Get validation error message
        String message = __(ex.getKey(), String.format("%s_%s", ex.getPrefix(), ex.getAttribute()), ex.getArgs());
        // Create body
        GeneralResponse responseBody = new GeneralResponse(false, message, ex.getAttribute());
        // Common handle logic
        commonLog(ex, response, HttpStatus.UNPROCESSABLE_ENTITY.value(), responseBody);
    }

    @ExceptionHandler(FileServingException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleFileServing(HttpServletRequest request, HttpServletResponse response, FileServingException ex) throws IOException {
        // Create body
        GeneralResponse responseBody = new GeneralResponse(false, __(ex.getKey()), "");
        // Common handle logic
        commonLog(ex, response, HttpStatus.NOT_FOUND.value(), responseBody);
    }

    @ExceptionHandler(FileUploadingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleFileServing(HttpServletRequest request, HttpServletResponse response, FileUploadingException ex) throws IOException {
        // Create body
        GeneralResponse responseBody = new GeneralResponse(false, __(ex.getKey()), "");
        // Common handle logic
        commonLog(ex, response, HttpStatus.BAD_REQUEST.value(), responseBody);
    }

    @ExceptionHandler(ExportCSVException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleExportCSV(HttpServletRequest request, HttpServletResponse response, ExportCSVException ex) throws IOException {
        // Create body
        GeneralResponse responseBody = new GeneralResponse(false, ex.getKey(), "");
        // Common handle logic
        commonLog(ex, response, HttpStatus.INTERNAL_SERVER_ERROR.value(), responseBody);
    }

    @ExceptionHandler(DataInconsistencyException.class)
    @ResponseStatus(HttpStatus.GONE)
    public void handleDataInconsistency(HttpServletRequest request, HttpServletResponse response, DataInconsistencyException ex) throws IOException {
        // Create body
        GeneralResponse responseBody = new GeneralResponse(false, ex.getClass().getSimpleName(), ex.getKey());
        // Common handle logic
        commonLog(ex, response, HttpStatus.GONE.value(), responseBody);
    }

    // ****************************
    // Private
    // ****************************

    /**
     * Common log process
     *
     * @param ex
     * @param response
     * @param sc
     */
    private void commonLog(Exception ex, HttpServletResponse response, int sc, GeneralResponse responseBody) throws IOException {
        logger.error("Server error: {}", ex.getMessage());

        // Set properties
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(sc);

        // Write body
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), responseBody);
    }

    /**
     * Translate message
     *
     * @param key
     */
    private String __(String key) {
        return messageSource.getMessage(key, null, key, LocaleContextHolder.getLocale());
    }

    /**
     * Translate message with attribute
     *
     * @param key
     */
    private String __(String key, String attribute, String ...arguments) {
        String[] args = ArrayUtils.addAll(new String[]{__(attribute)}, arguments);
        // Pre-translate all attributes before translate key
        return messageSource.getMessage(key, args, key, LocaleContextHolder.getLocale());
    }
}
