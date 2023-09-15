package diotviet.server.structures;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Data
public class Dataset<X, Y> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Dataset's key
     */
    private String key;

    /**
     * Dataset's label
     */
    private String label;

    /**
     * Dataset's hint
     */
    private String hint;

    /**
     * Dataset's stack group
     */
    private String stack;

    /**
     * Dataset's color
     */
    private String color;

    /**
     * Dataset's data
     */
    private List<DataPoint<X, Y>> data;

    // ****************************
    // Public API
    // ****************************

    /**
     * Create Dataset with key and data
     *
     * @param key
     * @param data
     * @param <X>
     * @param <Y>
     * @return
     */
    public static <X, Y> Dataset<X, Y> of(String key, String stack, String color) {
        // Create
        Dataset<X, Y> dataset = new Dataset<>();

        dataset.setKey(key);
        dataset.setStack(stack);
        dataset.setColor(color);
        dataset.setData(new ArrayList<>());

        return dataset;
    }

    /**
     * Add tuple to dataset
     *
     * @param dataPoint
     */
    public void add(DataPoint<X, Y> dataPoint) {
        this.data.add(dataPoint);
    }

    public void applyLocalizationWithSupplier(Function<String, String> supplier) {
        this.label = supplier.apply(this.key);
        this.hint = supplier.apply(this.key + "_hint");
    }
}
