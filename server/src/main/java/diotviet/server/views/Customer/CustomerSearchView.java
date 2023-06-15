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
     * Code
     *
     * @return
     */
    String getCode();

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
    @Value("#{T(org.apache.commons.lang3.time.DateFormatUtils).format(target.birthday, \"dd-MM-yyyy\")}")
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
    @Value("#{T(org.apache.commons.lang3.time.DateFormatUtils).format(target.createdAt, \"dd-MM-yyyy HH:mm:ss\")}")
    String getCreatedAt();

    /**
     * Get create date
     *
     * @return
     */
    @Value("#{T(org.apache.commons.lang3.time.DateFormatUtils).format(target.lastTransactionAt, \"dd-MM-yyyy HH:mm:ss\")}")
    String getLastTransactionAt();
}
