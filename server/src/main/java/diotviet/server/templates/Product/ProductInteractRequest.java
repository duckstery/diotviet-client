package diotviet.server.templates.Product;

import org.springframework.web.multipart.MultipartFile;

public record ProductInteractRequest(
        Long id,
        Long category,
        Long[] groups,
        String code,
        String title,
        String description,
        Long originalPrice,
        Long discount,
        String discountUnit,
        Long actualPrice,
        String measureUnit,
        String src,
        Integer weight,
        Boolean canBeAccumulated,
        Boolean isInBusiness,
        MultipartFile file
) {
}
