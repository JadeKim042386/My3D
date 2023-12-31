package joo.project.my3d.service;

import joo.project.my3d.domain.Alarm;
import joo.project.my3d.domain.ArticleLike;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.repository.AlarmRepository;
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

import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@DisplayName("비지니스 로직 - 모델 게시글 좋아요")
@ExtendWith(MockitoExtension.class)
class ArticleLikeServiceTest {
    @InjectMocks ArticleLikeService articleLikeService;
    @Mock ArticleLikeRepository articleLikeRepository;
    @Mock ArticleRepository articleRepository;
    @Mock UserAccountRepository userAccountRepository;
    @Mock private AlarmRepository alarmRepository;
    @Mock private AlarmService alarmService;

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
        // When
        articleLikeService.addArticleLike(articleId, userId);
        // Then
        then(articleRepository).should().getReferenceById(articleId);
        then(userAccountRepository).should().getReferenceByEmail(userId);
        then(articleLikeRepository).should().save(any(ArticleLike.class));
    }

    @DisplayName("좋아요 삭제")
    @Test
    void deleteArticleLike() {
        // Given
        Long articleId = 1L;
        ArticleLike articleLike = Fixture.getArticleLike();
        given(articleRepository.getReferenceById(articleId)).willReturn(articleLike.getArticle());
        willDoNothing().given(articleLikeRepository).deleteByArticleId(articleId);
        // When
        articleLikeService.deleteArticleLike(articleId);
        // Then
        then(articleRepository).should().getReferenceById(articleId);
        then(articleLikeRepository).should().deleteByArticleId(articleId);
    }
}
