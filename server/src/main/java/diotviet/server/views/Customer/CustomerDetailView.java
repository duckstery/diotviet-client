package diotviet.server.views.Customer;

import org.springframework.beans.factory.annotation.Value;

public interface CustomerDetailView {
    /**
     * Category ID
     *
     * @return
     */
    @Value("#{target.category.id}")
    long getCategoryId();
    /**
     * ID of groups
     *
     * @return
     */
    @Value("#{target.groups.![id]}")
    long[] getGroupIds();

    /**
     * Name of groups
     *
     * @return
     */
    @Value("#{T(String).join(', ', T(diotviet.server.utils.OtherUtils).sort(target.groups.![name]))}")
    String getGroups();

    /**
     * Email
     *
     * @return
     */
    String getEmail();

    /**
     * Facebook account
     *
     * @return
     */
    String getFacebook();

    /**
     * Description
     *
     * @return
     */
    String getDescription();
}
