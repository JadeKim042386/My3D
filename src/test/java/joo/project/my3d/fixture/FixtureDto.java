package joo.project.my3d.fixture;

import joo.project.my3d.domain.Address;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.domain.constant.DimUnit;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.*;
import joo.project.my3d.dto.request.CompanyAdminRequest;
import joo.project.my3d.dto.security.BoardPrincipal;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FixtureDto {

    public static UserAccountDto getUserAccountDto() {
        AddressDto addressDto = FixtureDto.getAddressDto();
        return UserAccountDto.of("jk042386@gmail.com", "pw", "01011111111", "Joo", addressDto, UserRole.USER);
    }

    public static UserAccountDto getUserAccountDto(String nickname, UserRole userRole) {
        AddressDto addressDto = FixtureDto.getAddressDto();
        return UserAccountDto.of(nickname + "@gmail.com", "pw", nickname, "01011111111", addressDto, userRole);
    }

    public static ArticleFileDto getArticleFileDto() {
        return ArticleFileDto.of(11L, 5555L, "test.stp", "uuid.stp", "stp");
    }

    public static ArticlePreviewDto getArticlesDto() {
        return FixtureDto.getArticlesDto(
                1L,
                "title",
                "content",
                ArticleType.MODEL,
                ArticleCategory.ARCHITECTURE
        );
    }
    public static ArticlePreviewDto getArticlesDto(Long id, String title, String content, ArticleType articleType, ArticleCategory articleCategory) {
        UserAccountDto userAccountDto = FixtureDto.getUserAccountDto();
        ArticleFileDto articleFileDto = FixtureDto.getArticleFileDto();
        return ArticlePreviewDto.of(
                id,
                0,
                userAccountDto.nickname(),
                title,
                content,
                articleType,
                articleCategory,
                articleFileDto,
                LocalDateTime.now()
        );
    }

    public static ArticleFileWithDimensionDto getArticleFileWithDimensionOptionWithDimensionDto() {
        DimensionOptionWithDimensionDto dimensionOptionWithDimensionDto = FixtureDto.getDimensionOptionWithDimensionDto();
        return ArticleFileWithDimensionDto.of(11L, 5555L, "test.stp", "uuid.stp", "stp", dimensionOptionWithDimensionDto);
    }

    public static ArticleDto getArticleDto(Long id, String title, String content, ArticleType articleType, ArticleCategory articleCategory) {
        UserAccountDto userAccountDto = FixtureDto.getUserAccountDto();
        ArticleFileWithDimensionDto articleFileDto = FixtureDto.getArticleFileWithDimensionOptionWithDimensionDto();
        return ArticleDto.of(
                id,
                userAccountDto,
                title,
                content,
                articleType,
                articleCategory,
                articleFileDto,
                LocalDateTime.now(),
                userAccountDto.email(),
                LocalDateTime.now(),
                userAccountDto.email()
        );
    }

    public static ArticleCommentDto getArticleCommentDto(String content) {
        return getArticleCommentDto(content, null);
    }

    public static ArticleCommentDto getArticleCommentDto(String content, Long parentCommentId) {
        UserAccountDto userAccountDto = FixtureDto.getUserAccountDto();
        return ArticleCommentDto.of(
                1L,
                1L,
                content,
                LocalDateTime.now(),
                userAccountDto.nickname(),
                userAccountDto.email(),
                parentCommentId
        );
    }

    public static ArticleWithCommentsDto getArticleWithCommentsAndLikeCountDto(String title, String content, ArticleType articleType, ArticleCategory articleCategory) {
        UserAccountDto userAccountDto = FixtureDto.getUserAccountDto();
        ArticleFileWithDimensionDto articleFileDto = FixtureDto.getArticleFileWithDimensionOptionWithDimensionDto();
        return ArticleWithCommentsDto.of(
                1L,
                userAccountDto.email(),
                userAccountDto.nickname(),
                articleFileDto,
                title,
                content,
                articleType,
                articleCategory,
                Set.of(FixtureDto.getArticleCommentDto("content")),
                LocalDateTime.now()
        );
    }

    public static ArticleFormDto getArticleFormDto(String title, String content, ArticleType articleType, ArticleCategory articleCategory) {
        UserAccountDto userAccountDto = FixtureDto.getUserAccountDto();
        ArticleFileWithDimensionDto articleFileDto = FixtureDto.getArticleFileWithDimensionOptionWithDimensionDto();
        return ArticleFormDto.of(
                1L,
                userAccountDto,
                articleFileDto,
                title,
                content,
                articleType,
                articleCategory,
                LocalDateTime.now(),
                userAccountDto.email(),
                LocalDateTime.now(),
                userAccountDto.email()
        );
    }

    public static DimensionDto getDimensionDto(Long dimensionId) {
        return DimensionDto.of(
                dimensionId,
                "너비",
                10.0f,
                DimUnit.MM
        );
    }

    public static DimensionDto getDimensionDto() {
        return FixtureDto.getDimensionDto(1L);
    }

    public static DimensionOptionDto getDimensionOptionDto(Long dimensionOptionId) {
        return DimensionOptionDto.of(
                dimensionOptionId,
                "option10"
        );
    }

    public static DimensionOptionDto getDimensionOptionDto() {
        return FixtureDto.getDimensionOptionDto(1L);
    }

    private static DimensionOptionWithDimensionDto getDimensionOptionWithDimensionDto() {
        DimensionDto dimensionDto = FixtureDto.getDimensionDto();
        return DimensionOptionWithDimensionDto.of(
                "option10",
                List.of(dimensionDto)
        );
    }

    public static UsernamePasswordAuthenticationToken getAuthentication(String nickname, UserRole userRole) {
        UserAccountDto userAccountDto = FixtureDto.getUserAccountDto(nickname, userRole);
        BoardPrincipal principal = BoardPrincipal.from(userAccountDto);
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
    }

    public static AddressDto getAddressDto() {
        return AddressDto.of("11111", "서울특별시", "111");
    }

    public static CompanyDto getCompanyDto() {
        return CompanyDto.of(2L, "company", "homepage");
    }

    public static BoardPrincipal getPrincipal(String nickname, UserRole userRole) {
        return BoardPrincipal.of(1L, nickname + "@gmail.com", "pw", "phone", nickname, userRole, Address.of(null, null, null), Map.of());
    }

    public static CompanyAdminRequest getCompanyAdminRequest() throws IllegalAccessException {
        CompanyAdminRequest request = new CompanyAdminRequest();
        FieldUtils.writeField(request, "companyName", "companyName", true);
        FieldUtils.writeField(request, "homepage", "homepage", true);
        return request;
    }
}
