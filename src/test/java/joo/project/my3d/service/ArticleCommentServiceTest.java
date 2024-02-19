package joo.project.my3d.service;

import joo.project.my3d.domain.Alarm;
import joo.project.my3d.domain.ArticleComment;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.dto.ArticleCommentDto;
import joo.project.my3d.exception.CommentException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.repository.ArticleCommentRepository;
import joo.project.my3d.repository.ArticleRepository;
import joo.project.my3d.service.impl.AlarmService;
import joo.project.my3d.service.impl.ArticleCommentService;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@DisplayName("비지니스 로직 - 댓글")
@ExtendWith(MockitoExtension.class)
class ArticleCommentServiceTest {
    @InjectMocks
    private ArticleCommentService articleCommentService;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleCommentRepository articleCommentRepository;

    @Mock
    private AlarmService alarmService;

    @DisplayName("댓글 저장")
    @Test
    void saveComment() throws IllegalAccessException {
        // Given
        ArticleCommentDto articleCommentDto = FixtureDto.getArticleCommentDto("content");
        ArticleComment articleComment = Fixture.getArticleComment("content");
        UserAccount sender = Fixture.getUserAccount();
        UserAccount receiver = Fixture.getUserAccount();
        Alarm alarm = Fixture.getAlarm(sender, receiver);
        FieldUtils.writeField(alarm, "id", 1L, true);
        given(articleRepository.getReferenceById(anyLong())).willReturn(articleComment.getArticle());
        given(articleCommentRepository.save(any(ArticleComment.class))).willReturn(articleComment);
        willDoNothing().given(alarmService).send(any(), isNull(), any(), any());
        // When
        articleCommentService.saveComment(articleCommentDto, sender, receiver);
        // Then
    }

    @DisplayName("[예외-없는 게시글] 댓글 저장")
    @Test
    void saveCommentNotExistArticle() {
        // Given
        ArticleCommentDto articleCommentDto = FixtureDto.getArticleCommentDto("content");
        UserAccount sender = Fixture.getUserAccount();
        UserAccount receiver = Fixture.getUserAccount();
        given(articleRepository.getReferenceById(anyLong()))
                .willThrow(new CommentException(ErrorCode.DATA_FOR_COMMENT_NOT_FOUND));
        // When
        assertThatThrownBy(() -> articleCommentService.saveComment(articleCommentDto, sender, receiver))
                .isInstanceOf(CommentException.class);
        // Then
    }

    @DisplayName("대댓글 저장")
    @Test
    void saveChildComment() throws IllegalAccessException {
        // Given
        ArticleCommentDto articleCommentDto = FixtureDto.getArticleCommentDto("content", 5L);
        ArticleComment articleComment = Fixture.getArticleComment("content");
        FieldUtils.writeField(articleComment, "id", 2L, true);
        UserAccount sender = Fixture.getUserAccount();
        UserAccount receiver = Fixture.getUserAccount();
        Alarm alarm = Fixture.getAlarm(sender, receiver);
        FieldUtils.writeField(alarm, "id", 1L, true);
        given(articleRepository.getReferenceById(anyLong())).willReturn(articleComment.getArticle());
        given(articleCommentRepository.findById(anyLong())).willReturn(Optional.of(articleComment));
        willDoNothing().given(alarmService).send(any(), isNull(), any(), any());
        // When
        articleCommentService.saveComment(articleCommentDto, sender, receiver);
        // Then
    }

    @DisplayName("댓글 삭제")
    @Test
    void deleteComment() {
        // Given
        Long articleCommentId = 1L;
        given(articleCommentRepository.findById(anyLong()))
                .willReturn(Optional.of(Fixture.getArticleComment("content")));
        given(articleCommentRepository.existsByIdAndUserAccountId(anyLong(), anyLong()))
                .willReturn(true);
        // When
        articleCommentService.deleteComment(articleCommentId, 1L);
        // Then
    }

    @DisplayName("댓글 삭제 - 작성자와 삭제 요청 유저가 다름")
    @Test
    void deleteCommentNotWriter() {
        // Given
        Long articleCommentId = 1L;
        given(articleCommentRepository.findById(anyLong()))
                .willReturn(Optional.of(Fixture.getArticleComment("content")));
        // When
        assertThatThrownBy(() -> articleCommentService.deleteComment(articleCommentId, 1L))
                .isInstanceOf(CommentException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_WRITER);
        // Then
    }
}
