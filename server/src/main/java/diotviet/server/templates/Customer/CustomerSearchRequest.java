package diotviet.server.templates.Customer;

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
public record CustomerSearchRequest(
        Long group,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        Date createAtFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        Date createAtTo,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        Date birthdayFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        Date birthdayTo,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        Date lastTransactionAtFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        Date lastTransactionAtTo,
        Boolean isMale,

        // Common part but cannot be inherited anymore
        String search,
        Integer page,
        Integer itemsPerPage
) {

}
