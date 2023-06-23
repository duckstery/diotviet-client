package diotviet.server.views.Order;

import org.springframework.beans.factory.annotation.Value;

public interface OrderSearchView {
    /**
     * ID
     *
     * @return
     */
    long getId();

    /**
     * Code
     *
     * @return
     */
    String getCode();

    @Value("#{target.customer.name}")
    String getCustomer();

    /**
     * Phone number
     *
     * @return
     */
    String getPhoneNumber();

    /**
     * Address
     *
     * @return
     */
    String getAddress();

    /**
     * Actual price
     *
     * @return
     */
    String getActualPrice();

    /**
     * Status
     *
     * @return
     */
    @Value("#{target.status.name().toLowerCase()}")
    String getStatus();

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
    @Value("#{T(diotviet.server.utils.OtherUtils).formatDateTime(target.createdAt, \"dd-MM-yyyy HH:mm:ss\")}")
    String getCreatedAt();

    /**
     * Get resolve date
     *
     * @return
     */
    @Value("#{T(diotviet.server.utils.OtherUtils).formatDateTime(target.resolvedAt, \"dd-MM-yyyy HH:mm:ss\")}")
    String resolvedAt();
}
