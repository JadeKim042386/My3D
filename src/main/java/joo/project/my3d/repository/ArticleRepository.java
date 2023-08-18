package joo.project.my3d.repository;

import com.querydsl.core.types.dsl.EnumExpression;
import com.querydsl.core.types.dsl.StringExpression;
import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.QArticle;
import joo.project.my3d.domain.constant.ArticleCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.util.List;

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
}
