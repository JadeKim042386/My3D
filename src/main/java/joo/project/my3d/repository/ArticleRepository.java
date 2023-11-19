package joo.project.my3d.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EnumExpression;
import com.querydsl.core.types.dsl.StringExpression;
import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.QArticle;
import joo.project.my3d.domain.constant.ArticleCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends
        JpaRepository<Article, Long>,
        QuerydslPredicateExecutor<Article>,
        QuerydslBinderCustomizer<QArticle>
{
    @Override
    default void customize(QuerydslBindings bindings, QArticle root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.title, root.content, root.articleCategory);
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.articleCategory).first(EnumExpression::eq);
    }

    Page<Article> findByArticleCategory(ArticleCategory articleCategory, Pageable pageable);

    List<Article> findAllByUserAccount_Email(String email);

    Optional<Article> findByIdAndUserAccount_Email(Long id, String email);

    @EntityGraph(value = "Article.fetchIndex", type = EntityGraph.EntityGraphType.LOAD)
    @Query("select a from Article a")
    Page<Article> findAllFetchIndex(Predicate predicate, Pageable pageable);

    @EntityGraph(value = "Article.fetchForm", type = EntityGraph.EntityGraphType.LOAD)
    @Query("select a from Article a where a.id = :id")
    Optional<Article> findByIdFetchForm(@Param("id") Long id);

    @EntityGraph(value = "Article.fetchDetail", type = EntityGraph.EntityGraphType.LOAD)
    @Query("select a from Article a where a.id = :id")
    Optional<Article> findByIdFetchDetail(@Param("id") Long id);
}
