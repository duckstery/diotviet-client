package diotviet.server.views.Staff;

import org.springframework.beans.factory.annotation.Value;

public interface StaffSearchView {
    /**
     * ID
     *
     * @return
     */
    long getId();

    /**
     * Role
     *
     * @return
     */
    @Value("#{target.user.role.getCode()}")
    Integer getRole();

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
     * Email
     *
     * @return
     */
    String getEmail();

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
     * Birthday
     *
     * @return
     */
    @Value("#{T(diotviet.server.utils.OtherUtils).formatDateTime(target.birthday, \"dd-MM-yyyy\")}")
    String getBirthday();

    /**
     * Gender
     *
     * @return
     */
    boolean getIsMale();

    /**
     * Is deactivated
     *
     * @return
     */
    boolean getIsDeactivated();

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
}
