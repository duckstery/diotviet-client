package diotviet.server.views;

/**
 * Entity provider
 *
 * @param <S> output
 * @param <T> param
 */
public interface EntityProvider<S, T> {
    /**
     * Provide Entity
     *
     * @param param
     * @return
     */
    public S provide(T param);
}
