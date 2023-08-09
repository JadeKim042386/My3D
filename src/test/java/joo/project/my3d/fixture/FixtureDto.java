package joo.project.my3d.fixture;

import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.ArticleCommentDto;
import joo.project.my3d.dto.ArticleDto;
import joo.project.my3d.dto.ArticleFileDto;
import joo.project.my3d.dto.UserAccountDto;

import java.time.LocalDateTime;

public class FixtureDto {

    public static UserAccountDto getUserAccountDto() {
        return UserAccountDto.of("joo", "joo@gmail.com", "Joo", UserRole.USER);
    }

    public static ArticleFileDto getArticleFileDto() {
        return ArticleFileDto.of(11L, 5555L, "test.stp", "stp");
    }

    public static ArticleDto getArticleDto(Long id, String title, String content, ArticleType articleType, ArticleCategory articleCategory) {
        UserAccountDto userAccountDto = FixtureDto.getUserAccountDto();
        ArticleFileDto articleFileDto = FixtureDto.getArticleFileDto();
        return ArticleDto.of(id, userAccountDto, articleFileDto, title, content, articleType, articleCategory, LocalDateTime.now(), userAccountDto.userId(), LocalDateTime.now(), userAccountDto.userId());
    }

    public static ArticleCommentDto getArticleCommentDto(String content) {
        UserAccountDto userAccountDto = FixtureDto.getUserAccountDto();
        return ArticleCommentDto.of(1L, content, 1L, userAccountDto, LocalDateTime.now(), userAccountDto.userId(), LocalDateTime.now(), userAccountDto.userId());
    }
}
