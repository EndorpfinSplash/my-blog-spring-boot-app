package by.jdeveloper.myblogbackspringbootapp.dao;

import by.jdeveloper.myblogbackspringbootapp.dto.NewCommentDto;
import by.jdeveloper.myblogbackspringbootapp.model.Comment;

import java.util.List;

public interface CommentRepository {

    Comment save(Long postId, NewCommentDto newCommentDto);

    Comment updateComment(Long commentId, Comment comment);

    List<Comment> findAllCommentsByPostId(Long postId);

    Comment findCommentByPostIdAndCommentId(Long postId, Long commentId);

}