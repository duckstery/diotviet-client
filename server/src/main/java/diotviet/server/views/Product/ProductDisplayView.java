package diotviet.server.views.Product;

public interface ProductDisplayView {
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
     * Weight
     *
     * @return
     */
    String getSrc();

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
