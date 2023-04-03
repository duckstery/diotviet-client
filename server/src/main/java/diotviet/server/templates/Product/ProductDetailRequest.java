package diotviet.server.templates.Product;

/**
 * Request for Product's detail
 *
 * @param id
 * @param category
 * @param groups
 * @param code
 * @param title
 * @param description
 * @param originalPrice
 * @param discount
 * @param discountUnit
 * @param actualPrice
 * @param measureUnit
 * @param src
 * @param weight
 * @param canBeAccumulated
 * @param isInBusiness
 */
public record ProductDetailRequest(
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
        String search
) {
}
