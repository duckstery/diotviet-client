package diotviet.server.services.imports;

import diotviet.server.views.EntityProvider;
import diotviet.server.views.Identifiable;
import org.dhatim.fastexcel.reader.Row;

import java.util.List;
import java.util.Objects;

public abstract class BaseImportService<E> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Index of code
     */
    private int index;
    /**
     * Code prefix
     */
    private String prefix;

    // ****************************
    // Abstraction
    // ****************************

    /**
     * Prepare cache, data, ...
     *
     * @return
     */
    public abstract List<E> prep();

    /**
     * Convert legacy model (KiotViet) to Entity
     *
     * @param row
     * @return
     */
    public abstract E convert(Row row);

    /**
     * Re-attach any relationship
     *
     * @param entity
     * @return
     */
    public abstract void pull(List<E> entity);

    /**
     * Import List of entity
     *
     * @param products
     */
    public abstract void runImport(List<E> entities);

    /**
     * Flush cache
     */
    public abstract void flush();

    // ****************************
    // Protected API
    // ****************************

    /**
     * Initialize code index
     *
     * @param prefix
     * @param provider
     */
    public void initializeCode(String prefix, EntityProvider<Identifiable, String> provider) {
        // Get Identifiable to retrieve code
        Identifiable identifiable = provider.provide(prefix + "%");
        // Save code prefix
        this.prefix = prefix;
        // Save code last index
        this.index = Integer.parseInt(Objects.isNull(identifiable) ? "1" : identifiable.getCode().substring(2));
    }

    /**
     * Generate code begin from index (index will increase after each generation)
     */
    public String generateCode() {
        return String.format("%s%05d", prefix, index++);
    }
}
