package diotviet.server.structures;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

@AllArgsConstructor
@Setter
public class Tuple<L, R> extends Pair<L, R> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Left value
     */
    private L left;
    /**
     * Right value
     */
    private R right;

    // ****************************
    // Public API
    // ****************************

    /**
     * Create Tuple
     *
     * @param left
     * @param right
     * @param <L>
     * @param <R>
     * @return
     */
    public static <L, R> Tuple<L, R> of(L left, R right) {
        return new Tuple<>(left, right);
    }

    /**
     * Get left (key)
     *
     * @return
     */
    @Override
    public L getLeft() {
        return this.left;
    }

    /**
     * Get right (value)
     *
     * @return
     */
    @Override
    public R getRight() {
        return this.right;
    }

    /**
     * Replace value
     *
     * @param value new value to be stored in this entry
     * @return
     */
    @Override
    public R setValue(R value) {
        // Save temp
        R temp = right;
        // Set value
        right = value;

        return temp;
    }
}
