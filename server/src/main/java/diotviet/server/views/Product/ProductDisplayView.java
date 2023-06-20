package diotviet.server.views.Product;

import diotviet.server.views.Visualize;

public interface ProductDisplayView extends Visualize {
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

    /**
     * Title
     *
     * @return
     */
    String getTitle();

    /**
     * Get original price
     *
     * @return
     */
    String getOriginalPrice();

    /**
     * Get discount
     *
     * @return
     */
    String getDiscount();

    /**
     * Measure unit
     *
     * @return
     */
    String getDiscountUnit();

    /**
     * Actual price
     *
     * @return
     */
    String getActualPrice();

    /**
     * Can be accumulated
     *
     * @return
     */
    Boolean getCanBeAccumulated();

    /**
     * Is in business
     *
     * @return
     */
    Boolean getIsInBusiness();
}
