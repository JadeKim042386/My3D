package joo.project.my3d.service;

import joo.project.my3d.domain.Alarm;
import joo.project.my3d.domain.ArticleLike;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.repository.ArticleLikeRepository;
import joo.project.my3d.repository.ArticleRepository;
import joo.project.my3d.repository.UserAccountRepository;
import joo.project.my3d.service.impl.ArticleLikeService;
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
    @InjectMocks
    ArticleLikeService articleLikeService;

    @Mock
    ArticleLikeRepository articleLikeRepository;

    @Mock
    ArticleRepository articleRepository;

    @Mock
    UserAccountRepository userAccountRepository;

    @DisplayName("좋아요 추가")
    @Test
    void addArticleLike() throws IllegalAccessException {
        // Given
        Long articleId = 1L;
        Long userId = 1L;
        ArticleLike articleLike = Fixture.getArticleLike();
        UserAccount sender = Fixture.getUserAccount();
        UserAccount receiver = Fixture.getUserAccount();
        Alarm alarm = Fixture.getAlarm(sender, receiver);
        FieldUtils.writeField(alarm, "id", 1L, true);
        given(articleRepository.getReferenceById(articleId)).willReturn(articleLike.getArticle());
        given(userAccountRepository.getReferenceById(anyLong())).willReturn(articleLike.getUserAccount());
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
        Long userId = 1L;
        willDoNothing().given(articleLikeRepository).deleteByArticleIdAndUserAccountId(anyLong(), anyLong());
        given(articleLikeRepository.countByArticleId(anyLong())).willReturn(1);
        // When
        int likeCount = articleLikeService.deleteArticleLike(articleId, userId);
        // Then
        assertThat(likeCount).isEqualTo(1);
    }
}
