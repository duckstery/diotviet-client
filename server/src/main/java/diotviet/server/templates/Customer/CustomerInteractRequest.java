package diotviet.server.templates.Customer;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

public record CustomerInteractRequest(
        Long id,
        Long[] groups,
        String code,
        String name,
        String address,
        String phoneNumber,
        String email,
        String facebook,
        Boolean isMale,
        String src,
        String description,
        MultipartFile file
) {
}
