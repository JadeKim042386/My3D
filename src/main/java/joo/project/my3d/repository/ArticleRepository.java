package joo.project.my3d.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EnumExpression;
import com.querydsl.core.types.dsl.StringExpression;
import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.QArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.util.Optional;

public interface ArticleRepository
        extends JpaRepository<Article, Long>, QuerydslPredicateExecutor<Article>, QuerydslBinderCustomizer<QArticle> {
    @Override
    default void customize(QuerydslBindings bindings, QArticle root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.title, root.articleCategory);
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.articleCategory).first(EnumExpression::eq);
    }

    Optional<Article> findByIdAndUserAccountId(Long articleId, Long userAccountId);

    boolean existsByIdAndUserAccountId(Long articleId, Long userAccountId);

    @EntityGraph(value = "Article.fetchPreview", type = EntityGraph.EntityGraphType.LOAD)
    Page<Article> findAll(Predicate predicate, Pageable pageable);

    @EntityGraph(value = "Article.fetchForm", type = EntityGraph.EntityGraphType.LOAD)
    @Query("select a from Article a where a.id = ?1")
    Optional<Article> findByIdFetchForm(Long id);

    @EntityGraph(value = "Article.fetchDetail", type = EntityGraph.EntityGraphType.LOAD)
    @Query("select a from Article a where a.id = ?1")
    Optional<Article> findByIdFetchDetail(Long id);

    @Modifying
    @Query("update Article a set a.likeCount = a.likeCount + 1")
    void addArticleLikeCount();

    @Modifying
    @Query("update Article a set a.likeCount = a.likeCount - 1")
    void deleteArticleLikeCount();

    @Query("select a.articleFile.fileName from Article a where a.id = ?1")
    Optional<String> findFileNameById(Long articleId);
}
