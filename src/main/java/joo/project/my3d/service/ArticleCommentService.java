package joo.project.my3d.service;

import joo.project.my3d.domain.ArticleComment;
import joo.project.my3d.dto.ArticleCommentDto;
import joo.project.my3d.repository.ArticleCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleCommentService {

    private final ArticleCommentRepository articleCommentRepository;


    public List<ArticleCommentDto> getComments(Long articleId) {
        return List.of();
    }

    @Transactional
    public void saveComment(ArticleCommentDto articleCommentDto) {
    }

    @Transactional
    public void updateComment(ArticleCommentDto articleCommentDto) {
    }

    @Transactional
    public void deleteComment(Long articleCommentId) {
    }
}
