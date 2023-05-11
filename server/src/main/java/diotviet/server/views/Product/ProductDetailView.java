package diotviet.server.views.Product;

import org.springframework.beans.factory.annotation.Value;
import java.util.ArrayList;

public interface ProductDetailView extends ProductSearchView {

    /**
     * Name of groups
     *
     * @return
     */
    @Value("#{T(String).join(', ', T(diotviet.server.utils.OtherUtils).sort(target.groups.![name]))}")
    String getGroups();

    /**
     * Description
     *
     * @return
     */
    String getDescription();

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
     * Weight
     *
     * @return
     */
    String getSrc();

    /**
     * Weight
     *
     * @return
     */
    String getWeight();
}
