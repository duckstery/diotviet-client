package diotviet.server.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import diotviet.server.templates.GeneralResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

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
        logger.error("Server error: {}", ex.getMessage());

        // Set properties
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        // Create body
        GeneralResponse responseBody = new GeneralResponse(false, ex.getMessage(), ex.getClass());

        // Write body
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), responseBody);
    }
}
