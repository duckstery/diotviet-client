package diotviet.server.structures;

import lombok.Data;

@Data
public class DataPoint<X, Y> {
    // ****************************
    // Properties
    // ****************************

    /**
     * Left value
     */
    private X x;
    /**
     * Right value
     */
    private Y y;

    // ****************************
    // Public API
    // ****************************

    /**
     * Create a Data point
     *
     * @param x
     * @param y
     * @return
     * @param <X>
     * @param <Y>
     */
    public static <X, Y> DataPoint<X, Y> of(X x, Y y) {
        // Create instance
        DataPoint<X, Y> point = new DataPoint<>();

        point.x = x;
        point.y = y;

        return point;
    }
}
