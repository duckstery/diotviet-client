package diotviet.server.traits;

/**
 * Optimistic Lock repository
 *
 * @param <ID>
 */
public interface OptimisticLockRepository<ID> {
    /**
     * Check if this version of Entity is exists
     *
     * @param id
     * @param version
     * @return
     */
    boolean existsByIdAndVersion(ID id, Long version);
}
