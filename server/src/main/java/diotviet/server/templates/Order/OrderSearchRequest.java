package diotviet.server.templates.Order;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Request for Customer search
 *
 * @param group
 * @param createAtFrom
 * @param createAtTo
 * @param birthdayFrom
 * @param birthdayTo
 * @param lastTransactionAtFrom
 * @param lastTransactionAtTo
 * @param isMale
 * @param search
 * @param page
 * @param itemsPerPage
 */
public record OrderSearchRequest(
        Long group,
        Long[] status,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        Date createAtFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        Date createAtTo,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        Date resolvedAtFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        Date resolvedAtTo,
        String priceFrom,
        String priceTo,
        Boolean isMale,

        // Common part but cannot be inherited anymore
        String search,
        Integer page,
        Integer itemsPerPage
) {

}
