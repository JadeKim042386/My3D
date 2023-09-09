package joo.project.my3d.dto.response;

import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.dto.ArticleDto;
import joo.project.my3d.dto.ArticleFileDto;
import joo.project.my3d.utils.LocalDateTimeUtils;

import java.time.LocalDateTime;
import java.util.List;

public record ArticleResponse(
        Long id,
        String nickname,
        ArticleFileResponse modelFile,
        String title,
        String content,
        ArticleType articleType,
        ArticleCategory articleCategory,
        int likeCount,
        String createdAt
) {
    public static ArticleResponse of(Long id, String nickname, ArticleFileResponse modelFile, String title, String content, ArticleType articleType, ArticleCategory articleCategory, int likeCount, LocalDateTime createdAt) {
        return new ArticleResponse(id, nickname, modelFile, title, content, articleType, articleCategory, likeCount, LocalDateTimeUtils.passedTime(createdAt));
    }

    public static ArticleResponse from(ArticleDto dto, ArticleFileDto articleFileDto) {
        return ArticleResponse.of(
                dto.id(),
                dto.userAccountDto().nickname(),
                ArticleFileResponse.from(articleFileDto),
                dto.title(),
                dto.content(),
                dto.articleType(),
                dto.articleCategory(),
                dto.likeCount(),
                dto.createdAt()
        );
    }
}
