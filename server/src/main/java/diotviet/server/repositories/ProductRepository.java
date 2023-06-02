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
    @Override
    @EntityGraph(attributePaths = {"category", "groups"})
    <S extends Product, R> R findBy(Predicate predicate, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction);

    @EntityGraph(attributePaths = {"category", "groups"})
    <T> T findById(Long id, Class<T> classType);

    boolean existsByCode(String code);
}
