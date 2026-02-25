package by.jdeveloper.myblogbackspringbootapp.repository;


import by.jdeveloper.myblogbackspringbootapp.dao.PostRepository;
import by.jdeveloper.myblogbackspringbootapp.dto.NewCommentDto;
import by.jdeveloper.myblogbackspringbootapp.model.Comment;
import by.jdeveloper.myblogbackspringbootapp.model.Post;
import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
@Primary
public class JdbcNativePostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Post> findAllByTitleContains(String search) {
        String sql = """
                select p.id, p.title, p.text, p.likes_count, p.tags,
                       count(c.id) as comments_count
                from post p
                left join comment c on p.id = c.post_id
                where p.title ilike ?
                group by p.id, p.title, p.text, p.likes_count, p.tags
                """;
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    List<String> tags = getTags(rs);
                    return Post.builder()
                            .id(rs.getLong("id"))
                            .title(rs.getString("title"))
                            .text(rs.getString("text"))
                            .tags(tags)
                            .likesCount(rs.getLong("likes_count"))
                            .build();
                },
                "%" + search + "%"
        );
    }

    @Override
    public List<Post> getAll() {
        String sql = """
                select p.id, p.title, p.text, p.likes_count, p.tags
                from post p
                """;
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    List<String> tags = getTags(rs);
                    return Post.builder()
                            .id(rs.getLong("id"))
                            .title(rs.getString("title"))
                            .text(rs.getString("text"))
                            .tags(tags)
                            .likesCount(rs.getLong("likes_count"))
                            .build();
                });
    }

    private List<String> getTags(ResultSet rs) throws SQLException {
        Object[] tagsArray = (Object[]) rs.getArray("tags").getArray();
        return Arrays.stream(tagsArray)
                .map(Object::toString)
                .toList();
    }

    @Override
    public Collection<Post> findAllByTagContains(String tag) {
        String sql = """
                select p.id, p.title, p.text, p.likes_count, p.tags,
                       count(c.id) as comments_count
                from post p
                left join comment c on p.id = c.post_id
                where ? = ANY(p.tags)
                group by p.id, p.title, p.text, p.likes_count, p.tags
                """;
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    List<String> tags = getTags(rs);
                    return Post.builder()
                            .id(rs.getLong("id"))
                            .title(rs.getString("title"))
                            .text(rs.getString("text"))
                            .tags(tags)
                            .likesCount(rs.getLong("likes_count"))
                            .build();
                },
                tag
        );
    }

    @Override
    public Post save(Post post) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO post(title, text, tags, likes_count) VALUES (?, ?, ?, ?)",
                    new String[]{"id"}
            );
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getText());
            ps.setLong(4, post.getLikesCount());

            Array sqlArray = connection.createArrayOf("text",
                    post.getTags().toArray(new String[0])
            );
            ps.setArray(3, sqlArray);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            post.setId(key.longValue());
        }
        return post;
    }

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
    public void deleteById(Long postId) {
        jdbcTemplate.update("delete from comment where post_id = ?", postId);
        jdbcTemplate.update("delete from image where post_id = ?", postId);
        jdbcTemplate.update("delete from post where id = ?", postId);
    }

    @Override
    public Post update(Long id, Post post) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    """
                            update post
                            set title = ?, text = ? , tags= ?
                            where id = ?
                            """,
                    new String[]{"id"}
            );
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getText());

            Array sqlArray = connection.createArrayOf("text",
                    post.getTags().toArray(new String[0])
            );
            ps.setArray(3, sqlArray);
            ps.setLong(4, id);
            return ps;
        });
        return post;
    }

    @Override
    public Optional<Post> findById(Long postId) {
        String sql = """
                select p.id, p.title, p.text, p.likes_count, p.tags,
                       count(c.id) as comments_count
                from post p
                left join comment c on p.id = c.post_id
                where p.id = ?
                group by p.id, p.title, p.text, p.likes_count, p.tags
                """;
        Post post = null;
        try {
            post = jdbcTemplate.queryForObject(
                    sql,
                    (rs, rowNum) -> {
                        List<String> tags = getTags(rs);

                        return Post.builder()
                                .id(rs.getLong("id"))
                                .title(rs.getString("title"))
                                .text(rs.getString("text"))
                                .tags(tags)
                                .likesCount(rs.getLong("likes_count"))
                                .commentsCount(rs.getLong("comments_count"))
                                .build();
                    },
                    postId
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
        return Optional.ofNullable(post);
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
    public Long likesIncrease(Long postId) {
        jdbcTemplate.update("update post set likes_count = likes_count + 1 where id = ?", postId);
        return jdbcTemplate.queryForObject("select likes_count from post where id = ?", Long.class, postId);
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

    @Override
    public void deleteByPostIdAndCommentId(Long postId, Long commentId) {
        jdbcTemplate.update(
                "delete from comment where id = ? and post_id = ?",
                commentId,
                postId
        );
    }

    @Override
    public void saveFile(Long postId, String fileName, byte[] data) {
        jdbcTemplate.update(
                "insert into image (post_id, file_name, data) values (?, ?, ?)",
                postId, fileName, data);
    }

    public boolean updateFileByPostId(Long postId, String fileName, byte[] data) {
        try {
            jdbcTemplate.update(
                    "update image set file_name= ?, data = ? where post_id= ?",
                    fileName, data, postId);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public byte[] getFileByPostId(Long postId) {
        String sql = "SELECT data FROM image WHERE post_id = ?";
        return jdbcTemplate.queryForObject(sql, byte[].class, postId);
    }

    @Override
    public Long countFilesByPostId(Long postId) {
        String sql = "SELECT count(*) FROM image WHERE post_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, postId);
    }

}