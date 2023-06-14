package diotviet.server.views.Customer;

import org.springframework.beans.factory.annotation.Value;

public interface CustomerSearchView {
    /**
     * ID
     *
     * @return
     */
    long getId();

    /**
     * Category name
     *
     * @return
     */
    @Value("#{target.category.name}")
    String getCategory();

    /**
     * Name
     *
     * @return
     */
    String getName();

    /**
     * Actual price
     *
     * @return
     */
    String getPhoneNumber();

    /**
     * Measure unit
     *
     * @return
     */
    String getAddress();

    /**
     * Weight
     *
     * @return
     */
    String getBirthday();

    /**
     * Gender
     *
     * @return
     */
    boolean getIsMale();

    /**
     * Point
     *
     * @return
     */
    Long getPoint();

    /**
     * Get creator
     *
     * @return
     */
    String getCreatedBy();

    /**
     * Get create date
     *
     * @return
     */
    // @Value("#{T(org.apache.commons.lang3.time.DateFormatUtils).format(target.createdAt, \"yyyy-MM-dd\")}")
    String getCreatedAt();

    /**
     * Get create date
     *
     * @return
     */
    String getLastTransactionAt();
}
