package joo.project.my3d.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserAccount is a Querydsl query type for UserAccount
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserAccount extends EntityPathBase<UserAccount> {

    private static final long serialVersionUID = 986525680L;

    public static final QUserAccount userAccount = new QUserAccount("userAccount");

    public final QAuditingFields _super = new QAuditingFields(this);

    public final SetPath<ArticleComment, QArticleComment> articleComments = this.<ArticleComment, QArticleComment>createSet("articleComments", ArticleComment.class, QArticleComment.class, PathInits.DIRECT2);

    public final SetPath<ArticleLike, QArticleLike> articleLikes = this.<ArticleLike, QArticleLike>createSet("articleLikes", ArticleLike.class, QArticleLike.class, PathInits.DIRECT2);

    public final SetPath<Article, QArticle> articles = this.<Article, QArticle>createSet("articles", Article.class, QArticle.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final StringPath email = createString("email");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final StringPath nickname = createString("nickname");

    public final StringPath userId = createString("userId");

    public final StringPath userPassword = createString("userPassword");

    public final EnumPath<joo.project.my3d.domain.constant.UserRole> userRole = createEnum("userRole", joo.project.my3d.domain.constant.UserRole.class);

    public QUserAccount(String variable) {
        super(UserAccount.class, forVariable(variable));
    }

    public QUserAccount(Path<? extends UserAccount> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserAccount(PathMetadata metadata) {
        super(UserAccount.class, metadata);
    }

}

