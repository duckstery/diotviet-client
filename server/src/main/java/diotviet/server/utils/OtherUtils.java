package diotviet.server.utils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Other utility
 */
public abstract class OtherUtils {

    // ****************************
    // Public API
    // ****************************

    /**
     * Try to get o1, if it's null, get o2 instead
     *
     * @param o1
     * @param o2
     * @return
     */
    public static Object get(Object o1, Object o2) {
        return Objects.nonNull(o1) ? o1 : o2;
    }

    /**
     * Sort
     *
     * @param list
     * @return
     */
    public static List<String> sort(List<String> list) {
        // Sort list
        Collections.sort(list);

        return list;
    }
}
