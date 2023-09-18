package joo.project.my3d.service;

import joo.project.my3d.domain.ArticleComment;
import joo.project.my3d.dto.ArticleCommentDto;
import joo.project.my3d.exception.CommentException;
import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.repository.ArticleCommentRepository;
import joo.project.my3d.repository.ArticleRepository;
import joo.project.my3d.repository.UserAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@DisplayName("비지니스 로직 - 댓글")
@ExtendWith(MockitoExtension.class)
class ArticleCommentServiceTest {
    @InjectMocks private ArticleCommentService articleCommentService;
    @Mock private ArticleRepository articleRepository;
    @Mock private UserAccountRepository userAccountRepository;
    @Mock private ArticleCommentRepository articleCommentRepository;

    @DisplayName("게시글 ID로 댓글 조회")
    @Test
    void getCommentWithArticleId() {
        // Given
        Long articleId = 1L;
        ArticleComment articleComment = Fixture.getArticleComment("content");
        given(articleCommentRepository.findByArticleId(articleId)).willReturn(List.of(articleComment));
        // When
        List<ArticleCommentDto> comments = articleCommentService.getComments(articleId);
        // Then
        assertThat(comments)
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("content", articleComment.getContent());
        then(articleCommentRepository).should().findByArticleId(articleId);
    }

    @DisplayName("댓글 저장")
    @Test
    void saveComment() {
        // Given
        ArticleCommentDto articleCommentDto = FixtureDto.getArticleCommentDto("content");
        ArticleComment articleComment = Fixture.getArticleComment("content");
        given(articleRepository.getReferenceById(articleCommentDto.articleId())).willReturn(articleComment.getArticle());
        given(userAccountRepository.getReferenceById(articleCommentDto.userAccountDto().email())).willReturn(articleComment.getUserAccount());
        given(articleCommentRepository.save(any(ArticleComment.class))).willReturn(articleComment);
        // When
        articleCommentService.saveComment(articleCommentDto);
        // Then
        then(articleRepository).should().getReferenceById(articleCommentDto.articleId());
        then(userAccountRepository).should().getReferenceById(articleCommentDto.userAccountDto().email());
        then(articleCommentRepository).should().save(any(ArticleComment.class));
    }

    @DisplayName("[예외-없는 게시글] 댓글 저장")
    @Test
    void saveCommentNotExistArticle() {
        // Given
        ArticleCommentDto articleCommentDto = FixtureDto.getArticleCommentDto("content");
        given(articleRepository.getReferenceById(articleCommentDto.articleId())).willThrow(EntityNotFoundException.class);
        // When
        articleCommentService.saveComment(articleCommentDto);
        // Then
        then(articleRepository).should().getReferenceById(articleCommentDto.articleId());
        then(userAccountRepository).shouldHaveNoInteractions();
        then(articleCommentRepository).shouldHaveNoInteractions();
    }

    @DisplayName("대댓글 저장")
    @Test
    void saveChildComment() {
        // Given
        ArticleCommentDto articleCommentDto = FixtureDto.getArticleCommentDto("content", 5L);
        ArticleComment articleComment = Fixture.getArticleComment("content");
        given(articleRepository.getReferenceById(articleCommentDto.articleId())).willReturn(articleComment.getArticle());
        given(userAccountRepository.getReferenceById(articleCommentDto.userAccountDto().email())).willReturn(articleComment.getUserAccount());
        given(articleCommentRepository.getReferenceById(articleCommentDto.parentCommentId())).willReturn(articleComment);
        // When
        articleCommentService.saveComment(articleCommentDto);
        // Then
        then(articleRepository).should().getReferenceById(articleCommentDto.articleId());
        then(userAccountRepository).should().getReferenceById(articleCommentDto.userAccountDto().email());
        then(articleCommentRepository).should().getReferenceById(articleCommentDto.parentCommentId());
    }

    @DisplayName("댓글 수정")
    @Test
    void updateComment() {
        // Given
        ArticleCommentDto articleCommentDto = FixtureDto.getArticleCommentDto("content");
        ArticleComment articleComment = Fixture.getArticleComment("content");
        given(articleCommentRepository.getReferenceById(articleCommentDto.id())).willReturn(articleComment);
        // When
        articleCommentService.updateComment(articleCommentDto);
        // Then
        then(articleCommentRepository).should().getReferenceById(articleCommentDto.id());
    }

    @DisplayName("[예외-없는 댓글] 댓글 수정")
    @Test
    void updateCommentNotExistComment() {
        // Given
        ArticleCommentDto articleCommentDto = FixtureDto.getArticleCommentDto("content");
        given(articleCommentRepository.getReferenceById(articleCommentDto.id())).willThrow(EntityNotFoundException.class);
        // When
        articleCommentService.updateComment(articleCommentDto);
        // Then
        then(articleCommentRepository).should().getReferenceById(articleCommentDto.id());
    }

    @DisplayName("댓글 삭제")
    @Test
     void deleteComment() {
        // Given
        Long articleCommentId = 1L;
        ArticleCommentDto articleCommentDto = FixtureDto.getArticleCommentDto("content");
        given(articleCommentRepository.getReferenceById(articleCommentId)).willReturn(Fixture.getArticleComment("content"));
        willDoNothing().given(articleCommentRepository).deleteById(articleCommentId);
        // When
        articleCommentService.deleteComment(articleCommentId, articleCommentDto.userAccountDto().email());
        // Then
        then(articleCommentRepository).should().getReferenceById(articleCommentId);
        then(articleCommentRepository).should().deleteById(articleCommentId);
    }

    @DisplayName("댓글 삭제 - 작성자와 삭제 요청 유저가 다름")
    @Test
    void deleteCommentNotWriter() {
        // Given
        Long articleCommentId = 1L;
        given(articleCommentRepository.getReferenceById(articleCommentId)).willReturn(Fixture.getArticleComment("content"));
        // When
        assertThatThrownBy(() -> articleCommentService.deleteComment(articleCommentId, "a"))
                .isInstanceOf(CommentException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_WRITER);
        // Then
        then(articleCommentRepository).should().getReferenceById(articleCommentId);
    }
}
