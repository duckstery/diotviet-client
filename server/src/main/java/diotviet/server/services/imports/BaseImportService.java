package diotviet.server.services.imports;

import org.dhatim.fastexcel.reader.Row;

import java.util.List;

public interface BaseImportService<E> {
    /**
     * Prepare cache, data, ...
     *
     * @return
     */
    List<E> prep();

    /**
     * Convert legacy model (KiotViet) to Entity
     *
     * @param row
     * @return
     */
    E convert(Row row);

    /**
     * Re-attach any relationship
     *
     * @param entity
     * @return
     */
    void pull(List<E> entity);

    /**
     * Import List of entity
     *
     * @param products
     */
    void runImport(List<E> entities);

    /**
     * Flush cache
     */
    void flush();
}
