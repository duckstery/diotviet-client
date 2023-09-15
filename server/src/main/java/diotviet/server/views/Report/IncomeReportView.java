package diotviet.server.views.Report;

import java.time.LocalDate;

public interface IncomeReportView {
    /**
     * Get date
     *
     * @return
     */
    LocalDate getDate();

    /**
     * Get expected income
     *
     * @return
     */
    Long getExpectedIncome();

    /**
     * Get real income inside
     *
     * @return
     */
    Long getRealIncomeInside();

    /**
     * Get real income outside
     *
     * @return
     */
    Long getRealIncomeOutside();

    /**
     * Usage
     *
     * @return
     */
    Long getUsage();
}
