package joo.project.my3d.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QArticleFile is a Querydsl query type for ArticleFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QArticleFile extends EntityPathBase<ArticleFile> {

    private static final long serialVersionUID = -1011445856L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QArticleFile articleFile = new QArticleFile("articleFile");

    public final QAuditingFields _super = new QAuditingFields(this);

    public final QArticle article;

    public final NumberPath<Long> byteSize = createNumber("byteSize", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final StringPath fileExtension = createString("fileExtension");

    public final StringPath fileName = createString("fileName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public QArticleFile(String variable) {
        this(ArticleFile.class, forVariable(variable), INITS);
    }

    public QArticleFile(Path<? extends ArticleFile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QArticleFile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QArticleFile(PathMetadata metadata, PathInits inits) {
        this(ArticleFile.class, metadata, inits);
    }

    public QArticleFile(Class<? extends ArticleFile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.article = inits.isInitialized("article") ? new QArticle(forProperty("article"), inits.get("article")) : null;
    }

}

