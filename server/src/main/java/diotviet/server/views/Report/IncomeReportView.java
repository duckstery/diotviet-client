package diotviet.server.views.Report;

public interface IncomeReportView {
    /**
     * Get date
     *
     * @return
     */
    String getTime();

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
