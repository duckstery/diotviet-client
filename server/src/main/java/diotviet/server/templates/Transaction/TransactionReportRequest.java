package diotviet.server.templates.Transaction;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Request for Transaction search,
 *
 * @param from
 * @param to
 */
public record TransactionReportRequest(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate from,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate to,
        String displayMode
) {
}
