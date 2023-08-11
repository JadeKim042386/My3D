package joo.project.my3d.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QArticle is a Querydsl query type for Article
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QArticle extends EntityPathBase<Article> {

    private static final long serialVersionUID = 1090570244L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QArticle article = new QArticle("article");

    public final QAuditingFields _super = new QAuditingFields(this);

    public final EnumPath<joo.project.my3d.domain.constant.ArticleCategory> articleCategory = createEnum("articleCategory", joo.project.my3d.domain.constant.ArticleCategory.class);

    public final SetPath<ArticleComment, QArticleComment> articleComments = this.<ArticleComment, QArticleComment>createSet("articleComments", ArticleComment.class, QArticleComment.class, PathInits.DIRECT2);

    public final QArticleFile articleFile;

    public final SetPath<ArticleLike, QArticleLike> articleLikes = this.<ArticleLike, QArticleLike>createSet("articleLikes", ArticleLike.class, QArticleLike.class, PathInits.DIRECT2);

    public final EnumPath<joo.project.my3d.domain.constant.ArticleType> articleType = createEnum("articleType", joo.project.my3d.domain.constant.ArticleType.class);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final StringPath title = createString("title");

    public final QUserAccount userAccount;

    public QArticle(String variable) {
        this(Article.class, forVariable(variable), INITS);
    }

    public QArticle(Path<? extends Article> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QArticle(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QArticle(PathMetadata metadata, PathInits inits) {
        this(Article.class, metadata, inits);
    }

    public QArticle(Class<? extends Article> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.articleFile = inits.isInitialized("articleFile") ? new QArticleFile(forProperty("articleFile"), inits.get("articleFile")) : null;
        this.userAccount = inits.isInitialized("userAccount") ? new QUserAccount(forProperty("userAccount")) : null;
    }

}

