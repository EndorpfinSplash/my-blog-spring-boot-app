package by.jdeveloper.myblogbackspringbootapp.repository;


import by.jdeveloper.myblogbackspringbootapp.dao.CommentRepository;
import by.jdeveloper.myblogbackspringbootapp.dto.NewCommentDto;
import by.jdeveloper.myblogbackspringbootapp.model.Comment;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;

@Repository
@Primary
@AllArgsConstructor
public class JdbcNativeCommentRepository implements CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Comment save(Long postId, NewCommentDto newCommentDto) {
        Comment comment = new Comment();
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO comment(text, post_id) VALUES (?, ?)",
                    new String[]{"id"}
            );
            ps.setString(1, newCommentDto.getText());
            ps.setLong(2, postId);

            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            comment.setId(key.longValue());
            comment.setText(newCommentDto.getText());
            comment.setPostId(postId);
        }
        return comment;
    }

    @Override
    public Comment updateComment(Long commentId, Comment comment) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    """
                            update comment set text = ?
                            where id = ?
                            """,
                    new String[]{"id"}
            );
            ps.setString(1, comment.getText());
            ps.setLong(2, commentId);
            return ps;
        });
        return comment;
    }


    @Override
    public List<Comment> findAllCommentsByPostId(Long postId) {
        String sql = """
                select c.id, c.text, c.post_id
                from comment c
                where c.post_id = ?
                """;
        try {
            return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Comment.class), postId);
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Comment findCommentByPostIdAndCommentId(Long postId, Long commentId) {
        String sql = """
                select c.id, c.text, c.post_id
                from comment c
                where c.post_id = ? and c.id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> Comment.builder()
                        .id(rs.getLong("id"))
                        .text(rs.getString("text"))
                        .postId(rs.getLong("post_id"))
                        .build(),
                postId,
                commentId
        );
    }

}