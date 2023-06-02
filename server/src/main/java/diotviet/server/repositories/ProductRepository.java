package diotviet.server.repositories;

import com.querydsl.core.types.Predicate;
import diotviet.server.entities.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.function.Function;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, QuerydslPredicateExecutor<Product> {
    /**
     * Find by multiple condition
     *
     * @param predicate
     * @param queryFunction
     * @return
     * @param <S>
     * @param <R>
     */
    @Override
    @EntityGraph(attributePaths = {"category", "groups"})
    <S extends Product, R> R findBy(Predicate predicate, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction);

    /**
     * Find by id
     *
     * @param id
     * @param classType
     * @return
     * @param <T>
     */
    @EntityGraph(attributePaths = {"category", "groups"})
    <T> T findById(Long id, Class<T> classType);

    /**
     * Check if exist by code
     *
     * @param code
     * @return
     */
    boolean existsByCode(String code);

    /**
     * Find first Product where code like "?" Order by code desc
     *
     * @param code
     * @return
     */
    Product findFirstByCodeLikeOrderByCodeDesc(String code);
}
