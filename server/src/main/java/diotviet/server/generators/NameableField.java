package diotviet.server.generators;

import com.opencsv.bean.AbstractBeanField;
import diotviet.server.views.Nameable;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * Convert Nameable Object to CSV line
 *
 * @param <T>
 * @param <I>
 */
public class NameableField<T, I> extends AbstractBeanField<T, I> {
    /**
     * Convert CSV field to Object
     *
     * @param s
     * @return
     */
    @Override
    protected Object convert(String s) {
        try {
            if (Objects.isNull(s) || s.isBlank()) {
                return null;
            }

            // Create instance of field's type and cast to Nameable
            Nameable identifiable = (Nameable) getField().getType().getDeclaredConstructor().newInstance();
            // Set ID
            identifiable.setName(s);

            return identifiable;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert Object to CSV field
     *
     * @param value
     * @return
     */
    @Override
    protected String convertToWrite(Object value) {
        if (Objects.isNull(value)) {
            return "";
        }

        // Cast to Nameable and get name
        return ((Nameable) value).getName();
    }
}
