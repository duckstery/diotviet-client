package diotviet.server.templates.Product;

import lombok.NoArgsConstructor;

/**
 * Request for Product search
 *
 * @param category
 * @param groups
 * @param search
 * @param minPrice
 * @param maxPrice
 * @param canBeAccumulated
 * @param isInBusiness
 * @param page
 * @param itemsPerPage
 */
public record ProductSearchRequest(
        Long category,
        Long[] groups,
        String minPrice,
        String maxPrice,
        Boolean canBeAccumulated,
        Boolean isInBusiness,

        // Common part but cannot be inherited anymore
        String search,
        Integer page,
        Integer itemsPerPage
) {
    /**
     * Default constructor
     */
    public ProductSearchRequest() {
        this(null, null, null, null, null, null, null, null, null);
    }
}
