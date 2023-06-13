package diotviet.server.templates.Customer;

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
public record CustomerSearchRequest(
        Long[] categories,
        Long group,
        String minPrice,
        String maxPrice,
        Boolean canBeAccumulated,
        Boolean isInBusiness,

        // Common part but cannot be inherited anymore
        String search,
        Integer page,
        Integer itemsPerPage
) {

}
