package joo.project.my3d.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EnumExpression;
import com.querydsl.core.types.dsl.StringExpression;
import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.QArticle;
import joo.project.my3d.domain.UserAccount;
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

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.LOAD;

public interface ArticleRepository
        extends JpaRepository<Article, Long>, QuerydslPredicateExecutor<Article>, QuerydslBinderCustomizer<QArticle> {
    @Override
    default void customize(QuerydslBindings bindings, QArticle root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.title, root.articleCategory);
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.articleCategory).first(EnumExpression::eq);
    }

    /**
     * 수정자와 작성자가 동일할 경우에만 반환
     */
    @Query("select distinct a " + "from Article a "
            + "left outer join fetch a.articleFile af "
            + "left outer join fetch af.dimensionOption do "
            + "left outer join fetch do.dimensions "
            + "where a.id = ?1")
    Optional<Article> findByIdAndUserAccountId(Long articleId, Long userAccountId);

    @Query("select (a.userAccount.id = ?2) from Article a where a.id = ?1")
    boolean existsByIdAndUserAccountId(Long articleId, Long userAccountId);

    @EntityGraph(
            attributePaths = {"userAccount", "articleFile"},
            type = LOAD)
    Page<Article> findAll(Predicate predicate, Pageable pageable);

    @Query("select distinct a " + "from Article a "
            + "left outer join fetch a.userAccount ua "
            + "left outer join fetch a.articleFile af "
            + "left outer join fetch af.dimensionOption do "
            + "left outer join fetch do.dimensions "
            + "where a.id = ?1")
    Optional<Article> findByIdFetchForm(Long id);

    @Query("select distinct a " + "from Article a "
            + "left outer join fetch a.userAccount ua "
            + "left outer join fetch a.articleFile af "
            + "left outer join fetch af.dimensionOption do "
            + "left outer join fetch do.dimensions "
            + "left outer join fetch a.articleComments ac "
            + "left outer join fetch ac.userAccount "
            + "where a.id = ?1")
    Optional<Article> findByIdFetchDetail(Long id);

    @Query("select distinct a " + "from Article a "
            + "left outer join fetch a.userAccount ua "
            + "left outer join fetch a.articleFile af "
            + "left outer join fetch af.dimensionOption do "
            + "left outer join fetch do.dimensions "
            + "left outer join fetch a.articleComments ac "
            + "left outer join fetch ac.childComments "
            + "left outer join fetch ac.userAccount "
            + "left outer join fetch a.articleLikes "
            + "left outer join fetch a.alarms "
            + "where a.id = ?1")
    Optional<Article> findByIdFetchAll(Long id);

    @Modifying
    @Query("update Article a set a.likeCount = a.likeCount + 1")
    void addArticleLikeCount();

    @Modifying
    @Query("update Article a set a.likeCount = a.likeCount - 1")
    void deleteArticleLikeCount();

    @Query("select a.articleFile.fileName from Article a where a.id = ?1")
    Optional<String> findFileNameById(Long articleId);

    @Query("select a.userAccount from Article a where a.id = ?1")
    Optional<UserAccount> findUserAccountById(Long articleId);
}
