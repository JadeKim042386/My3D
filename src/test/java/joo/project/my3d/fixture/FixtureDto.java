package joo.project.my3d.fixture;

import joo.project.my3d.domain.constant.*;
import joo.project.my3d.dto.*;
import joo.project.my3d.dto.security.BoardPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class FixtureDto {

    public static UserAccountDto getUserAccountDto() {
        AddressDto addressDto = FixtureDto.getAddressDto();
        return UserAccountDto.of("jk042386@gmail.com", "pw", "01011111111", "Joo", addressDto, true, UserRole.USER);
    }

    public static UserAccountDto getUserAccountDto(String nickname, UserRole userRole, boolean isSignUp) {
        AddressDto addressDto = FixtureDto.getAddressDto();
        return UserAccountDto.of(nickname + "@gmail.com", "pw", nickname, "01011111111", addressDto, isSignUp, userRole);
    }

    public static ArticleFileDto getArticleFileDto() {
        return ArticleFileDto.of(11L, 5555L, "test.stp", "uuid.stp", "stp");
    }

    public static ArticleDto getArticleDto(Long id, String title, String summary, String content, ArticleType articleType, ArticleCategory articleCategory) {
        UserAccountDto userAccountDto = FixtureDto.getUserAccountDto();
        PriceDto priceDto = FixtureDto.getPriceDto();
        return ArticleDto.of(
                id,
                userAccountDto,
                title,
                summary,
                content,
                articleType,
                articleCategory,
                1,
                priceDto,
                LocalDateTime.now(),
                userAccountDto.email(),
                LocalDateTime.now(),
                userAccountDto.email()
        );
    }

    public static ArticleCommentDto getArticleCommentDto(String content) {
        UserAccountDto userAccountDto = FixtureDto.getUserAccountDto();
        return ArticleCommentDto.of(1L, content, 1L, null, userAccountDto, LocalDateTime.now(), userAccountDto.email(), LocalDateTime.now(), userAccountDto.email());
    }

    public static ArticleCommentDto getArticleCommentDto(String content, Long parentCommentId) {
        UserAccountDto userAccountDto = FixtureDto.getUserAccountDto();
        return ArticleCommentDto.of(1L, content, 1L, parentCommentId, userAccountDto, LocalDateTime.now(), userAccountDto.email(), LocalDateTime.now(), userAccountDto.email());
    }

    public static ArticleWithCommentsAndLikeCountDto getArticleWithCommentsAndLikeCountDto(String title, String summary, String content, ArticleType articleType, ArticleCategory articleCategory) {
        UserAccountDto userAccountDto = FixtureDto.getUserAccountDto();
        ArticleFileDto articleFileDto = FixtureDto.getArticleFileDto();
        PriceDto priceDto = FixtureDto.getPriceDto();
        GoodOptionWithDimensionDto goodOptionWithDimensionDto = FixtureDto.getGoodOptionWithDimensionDto();
        return ArticleWithCommentsAndLikeCountDto.of(
                1L,
                userAccountDto,
                Set.of(articleFileDto),
                title,
                summary,
                content,
                articleType,
                articleCategory,
                Set.of(),
                2,
                priceDto,
                List.of(goodOptionWithDimensionDto),
                LocalDateTime.now(),
                userAccountDto.email(),
                LocalDateTime.now(),
                userAccountDto.email()
        );
    }


    public static DimensionDto getDimensionDto(Long dimensionId) {
        return DimensionDto.of(
                dimensionId,
                1L,
                "너비",
                10.0f,
                DimUnit.MM
        );
    }

    public static DimensionDto getDimensionDto() {
        return FixtureDto.getDimensionDto(3L);
    }

    public static GoodOptionDto getGoodOptionDto(Long goodOptionId) {
        return GoodOptionDto.of(
                goodOptionId,
                1L,
                "option10",
                3000,
                "LSA",
                "lesin"
        );
    }

    public static GoodOptionDto getGoodOptionDto() {
        return FixtureDto.getGoodOptionDto(3L);
    }

    private static GoodOptionWithDimensionDto getGoodOptionWithDimensionDto() {
        DimensionDto dimensionDto = FixtureDto.getDimensionDto();
        return GoodOptionWithDimensionDto.of(
                1L,
                "option10",
                3000,
                "LSA",
                "lesin",
                List.of(dimensionDto)
        );
    }

    public static PriceDto getPriceDto() {
        return PriceDto.of(3000, 3000);
    }

    public static UsernamePasswordAuthenticationToken getAuthentication(String nickname, UserRole userRole) {
        UserAccountDto userAccountDto = FixtureDto.getUserAccountDto(nickname, userRole, true);
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

    public static OrdersDto getOrdersDto() {
        AddressDto addressDto = FixtureDto.getAddressDto();
        UserAccountDto userAccountDto = FixtureDto.getUserAccountDto();
        CompanyDto companyDto = FixtureDto.getCompanyDto();
        return OrdersDto.of(4L, "example", addressDto, OrderStatus.EXAMINE, userAccountDto, companyDto);
    }
}
