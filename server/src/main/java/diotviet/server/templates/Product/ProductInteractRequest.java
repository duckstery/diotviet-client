package diotviet.server.templates.Product;

import org.springframework.web.multipart.MultipartFile;

public record ProductInteractRequest(
        Long id,
        Long category,
        Long[] groups,
        String code,
        String title,
        String description,
        String originalPrice,
        String discount,
        String discountUnit,
        String actualPrice,
        String measureUnit,
        String src,
        String weight,
        Boolean canBeAccumulated,
        Boolean isInBusiness,
        MultipartFile file
) {
}
