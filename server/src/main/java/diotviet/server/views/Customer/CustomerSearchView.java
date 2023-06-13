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
    @Value("#{T(org.apache.commons.lang3.time.DateFormatUtils).format(target.birthday.name, \"yyyy-MM-dd\")}")
    String getBirthDay();

    /**
     * Gender
     *
     * @return
     */
    @Value("#{target.isMale ? 'M' : 'F'}")
    String getGender();

    /**
     * Point
     *
     * @return
     */
    Long getPoint();
}
