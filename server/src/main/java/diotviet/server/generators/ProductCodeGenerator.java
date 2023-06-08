package diotviet.server.generators;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.util.Properties;

/**
 * Code generator for Product
 */
public class ProductCodeGenerator implements IdentifierGenerator, Configurable {

    // ****************************
    // Properties
    // ****************************

    private static final int increment = 1;
    private static int index = 1;
    private String prefix = "MS";

    // ****************************
    // Public API
    // ****************************

    public static String generate(String prefix) {
        // Get current index
        int current = index;
        // Increase index
        index = index + increment;

        return String.format(prefix + "%05d", current);
    }

    // ****************************
    // Override API
    // ****************************

    @Override
    public void configure(Type type, Properties parameters, ServiceRegistry serviceRegistry) {
        prefix = parameters.getProperty("prefix");
    }

    @Override
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        return generate(this.prefix);
    }
}
