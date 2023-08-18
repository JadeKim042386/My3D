package joo.project.my3d.fixture;

import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.*;

import java.time.LocalDateTime;
import java.util.Set;

public class FixtureDto {

    public static UserAccountDto getUserAccountDto() {
        return UserAccountDto.of("jk042386@gmail.com", "pw", "Joo", UserRole.USER);
    }

    public static UserAccountDto getUserAccountDto(String nickname, UserRole userRole) {
        return UserAccountDto.of(nickname + "@gmail.com", "pw", nickname, userRole);
    }

    public static ArticleFileDto getArticleFileDto() {
        return ArticleFileDto.of(11L, 5555L, "test.stp", "uuid.stp", "stp");
    }

    public static ArticleDto getArticleDto(Long id, String title, String content, ArticleType articleType, ArticleCategory articleCategory) {
        UserAccountDto userAccountDto = FixtureDto.getUserAccountDto();
        ArticleFileDto articleFileDto = FixtureDto.getArticleFileDto();
        return ArticleDto.of(id, userAccountDto, articleFileDto, title, content, articleType, articleCategory, 1, LocalDateTime.now(), userAccountDto.email(), LocalDateTime.now(), userAccountDto.email());
    }

    public static ArticleCommentDto getArticleCommentDto(String content) {
        UserAccountDto userAccountDto = FixtureDto.getUserAccountDto();
        return ArticleCommentDto.of(1L, content, 1L, null, userAccountDto, LocalDateTime.now(), userAccountDto.email(), LocalDateTime.now(), userAccountDto.email());
    }

    public static ArticleCommentDto getArticleCommentDto(String content, Long parentCommentId) {
        UserAccountDto userAccountDto = FixtureDto.getUserAccountDto();
        return ArticleCommentDto.of(1L, content, 1L, parentCommentId, userAccountDto, LocalDateTime.now(), userAccountDto.email(), LocalDateTime.now(), userAccountDto.email());
    }

    public static ArticleWithCommentsAndLikeCountDto getArticleWithCommentsAndLikeCountDto(String title, String content, ArticleType articleType, ArticleCategory articleCategory) {
        UserAccountDto userAccountDto = FixtureDto.getUserAccountDto();
        ArticleFileDto articleFileDto = FixtureDto.getArticleFileDto();
        return ArticleWithCommentsAndLikeCountDto.of(
                1L,
                userAccountDto,
                articleFileDto,
                title,
                content,
                articleType,
                articleCategory,
                Set.of(),
                2,
                LocalDateTime.now(),
                userAccountDto.email(),
                LocalDateTime.now(),
                userAccountDto.email()
        );
    }
}
