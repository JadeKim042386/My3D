package joo.project.my3d.service;

import joo.project.my3d.domain.Alarm;
import joo.project.my3d.domain.ArticleLike;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.repository.ArticleLikeRepository;
import joo.project.my3d.repository.ArticleRepository;
import joo.project.my3d.repository.UserAccountRepository;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@DisplayName("비지니스 로직 - 모델 게시글 좋아요")
@ExtendWith(MockitoExtension.class)
class ArticleLikeServiceTest {
    @InjectMocks ArticleLikeService articleLikeService;
    @Mock ArticleLikeRepository articleLikeRepository;
    @Mock ArticleRepository articleRepository;
    @Mock UserAccountRepository userAccountRepository;

    @DisplayName("좋아요 추가")
    @Test
    void addArticleLike() throws IllegalAccessException {
        // Given
        Long articleId = 1L;
        String userId = "joo";
        ArticleLike articleLike = Fixture.getArticleLike();
        UserAccount userAccount = Fixture.getUserAccount();
        Alarm alarm = Fixture.getAlarm(userAccount);
        FieldUtils.writeField(alarm, "id", 1L, true);
        given(articleRepository.getReferenceById(articleId)).willReturn(articleLike.getArticle());
        given(userAccountRepository.getReferenceByEmail(userId)).willReturn(articleLike.getUserAccount());
        given(articleLikeRepository.save(any(ArticleLike.class))).willReturn(articleLike);
        given(articleLikeRepository.countByArticleId(anyLong())).willReturn(1);
        // When
        int likeCount = articleLikeService.addArticleLike(articleId, userId);
        // Then
        assertThat(likeCount).isEqualTo(1);
    }

    @DisplayName("좋아요 삭제")
    @Test
    void deleteArticleLike() {
        // Given
        Long articleId = 1L;
        String email = "a@gmail.com";
        willDoNothing().given(articleLikeRepository).deleteByArticleIdAndUserAccount_Email(anyLong(), anyString());
        given(articleLikeRepository.countByArticleId(anyLong())).willReturn(1);
        // When
        int likeCount = articleLikeService.deleteArticleLike(articleId, email);
        // Then
        assertThat(likeCount).isEqualTo(1);
    }
}
