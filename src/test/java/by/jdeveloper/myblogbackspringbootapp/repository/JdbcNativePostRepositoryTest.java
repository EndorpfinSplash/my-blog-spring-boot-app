package by.jdeveloper.myblogbackspringbootapp.repository;


import by.jdeveloper.myblogbackspringbootapp.dao.PostRepository;
import by.jdeveloper.myblogbackspringbootapp.model.Comment;
import by.jdeveloper.myblogbackspringbootapp.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(classes = {
        DataSourceConfiguration.class,
        JdbcNativePostRepository.class}
)
@TestPropertySource(locations = "classpath:test-application.properties")
class JdbcNativePostRepositoryTest {

    public static final byte[] FILE_STUB = {(byte) 137, 80, 78, 71};
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM image");

        jdbcTemplate.execute("DELETE FROM comment");
        jdbcTemplate.execute("ALTER TABLE comment ALTER COLUMN id RESTART WITH 1");

        jdbcTemplate.execute("DELETE FROM post");
        jdbcTemplate.execute("ALTER TABLE post ALTER COLUMN id RESTART WITH 1");

        jdbcTemplate.update("INSERT INTO post(title, text, tags, likes_count) VALUES (?, ?, ?, ?)",
                ps -> {
                    ps.setString(1, "Test title");
                    ps.setString(2, "test text");
                    java.sql.Array tagsArray = ps.getConnection().createArrayOf("VARCHAR", new String[]{"simple_tag"});
                    ps.setArray(3, tagsArray);
                    ps.setInt(4, 0);
                }
        );

        jdbcTemplate.update("INSERT INTO post(title, text, tags, likes_count) VALUES (?, ?, ?, ?)",
                ps -> {
                    ps.setString(1, "TEST title 2");
                    ps.setString(2, "test second");
                    java.sql.Array tagsArray = ps.getConnection().createArrayOf("VARCHAR", new String[]{"test_second"});
                    ps.setArray(3, tagsArray);
                    ps.setInt(4, 0);
                }
        );

        jdbcTemplate.update("INSERT INTO post(title, text, tags, likes_count) VALUES (?, ?, ?, ?)",
                ps -> {
                    ps.setString(1, "Title");
                    ps.setString(2, "simple text");
                    java.sql.Array tagsArray = ps.getConnection().createArrayOf("VARCHAR", new String[]{"simple_tag", "second_tag"});
                    ps.setArray(3, tagsArray);
                    ps.setInt(4, 0);
                }
        );

        jdbcTemplate.update("INSERT INTO comment(text, post_id) VALUES (?, ?)",
                ps -> {
                    ps.setString(1, "Comment text for post");
                    ps.setLong(2, 1L);
                }
        );

        jdbcTemplate.update("INSERT INTO comment(text, post_id) VALUES (?, ?)",
                ps -> {
                    ps.setString(1, "Second comment for first post");
                    ps.setLong(2, 1L);
                }
        );

        jdbcTemplate.update("INSERT INTO image( post_id, file_name, data) VALUES (?, ?, ?)",
                ps -> {
                    ps.setLong(1, 1L);
                    ps.setString(2, "Origin_file_name");
                    ps.setBytes(3, FILE_STUB);
                }
        );

    }

    @Test
    void findAllByExistedTitle_shouldReturnPosts() {
        Collection<by.jdeveloper.myblogbackspringbootapp.model.Post> posts = postRepository.findAllByTitleContains("test");

        assertEquals(2, posts.size());
        assertTrue(List.of(1L, 2L).containsAll(posts.stream().map(Post::getId).toList()));
    }

    @Test
    void findAllByNonExistedTitle_shouldReturnEmptyList() {
        Collection<Post> posts = postRepository.findAllByTitleContains("absent test");

        assertEquals(0, posts.size());
    }

    @Test
    void findAllByExistedTag_shouldReturnPosts() {
        Collection<Post> posts = postRepository.findAllByTagContains("simple_tag");

        assertEquals(2, posts.size());
        assertTrue(posts.stream().map(Post::getId).toList().containsAll(List.of(1L, 3L)));
    }

    @Test
    void findAllByNonExistedTag_shouldReturnEmptyList() {
        Collection<Post> posts = postRepository.findAllByTagContains("absent");

        assertEquals(0, posts.size());
    }


    @Test
    void save_shouldAddPostToDatabase() {
        Post post = Post.builder()
                .title("for save")
                .text("some text")
                .tags(List.of("saved_tag"))
                .build();
        postRepository.save(post);

        Post saved = postRepository.findById(4L).get();

        assertNotNull(saved);
        assertEquals(4L, saved.getId());
        assertEquals("for save", saved.getTitle());
        assertEquals("some text", saved.getText());
        assertEquals("saved_tag", saved.getTags().getFirst());
    }


    @Test
    void deleteById() {
        postRepository.deleteById(3L);

        List<Post> allPosts = postRepository.getAll();
        assertEquals(2, allPosts.size());
        assertTrue(allPosts.stream().noneMatch(u -> u.getId().equals(3L)));
    }

    @Test
    void update() {
        Post modifiedPost = Post.builder()
                .title("Modified title")
                .text("Modified text")
                .tags(List.of("saved_tag"))
                .build();
        postRepository.update(1L, modifiedPost);
    }

    @Test
    void findById() {
        Post post = postRepository.findById(3L).get();

        assertEquals("Title", post.getTitle());
        assertEquals("simple text", post.getText());
        assertEquals(0, post.getLikesCount());
        assertTrue(post.getTags().containsAll(List.of("simple_tag", "second_tag")));
        assertEquals(2, post.getTags().size());
    }

    @Test
    void findAllCommentsByPostId() {
        List<Comment> allCommentsByPostId = postRepository.findAllCommentsByPostId(1L);
        assertEquals(2, allCommentsByPostId.size());
        assertEquals("Comment text for post", allCommentsByPostId.getFirst().getText());
    }

    @Test
    void likesIncrease() {
        Long likes = postRepository.likesIncrease(2L);

        assertEquals(1, likes);
    }

    @Test
    void findCommentByPostIdAndCommentId() {
        Comment comment = postRepository.findCommentByPostIdAndCommentId(1L, 2L);

        assertEquals(2L, comment.getId());
        assertEquals(1L, comment.getPostId());
        assertEquals("Second comment for first post", comment.getText());
    }

    @Test
    void deleteByPostIdAndCommentId() {
        postRepository.deleteByPostIdAndCommentId(1L, 2L);

        List<Comment> allComments = postRepository.findAllCommentsByPostId(1L);
        assertEquals(1, allComments.size());
        assertTrue(allComments.stream().noneMatch(comment -> comment.getId().equals(2L)));
    }

    @Test
    void saveFile() {
        byte[] fileStub = new byte[]{(byte) 137, 80, 78, 71};
        postRepository.saveFile(2L, "test_name", fileStub);

        byte[] savedFile = postRepository.getFileByPostId(2L);
        assertArrayEquals(fileStub,savedFile);
//        assertEquals("test file", );
    }

    @Test
    void updateFileByPostId() {
        byte[] newFile =new byte[]{(byte) 147, 90, 88, 81};
        boolean isUpdated = postRepository.updateFileByPostId(1L, "updated_file_name", newFile);

        byte[] updatedFile = postRepository.getFileByPostId(1L);
        assertTrue(isUpdated);
        assertArrayEquals(newFile, updatedFile);
    }

    @Test
    void getFileByPostId() {
        byte[] originFile = postRepository.getFileByPostId(1L);

        assertArrayEquals(FILE_STUB, originFile);
    }

    @Test
    void countFilesByPostId() {
        Long filesQuantityPost1 = postRepository.countFilesByPostId(1L);
        assertEquals(1, filesQuantityPost1);
        Long filesQuantityPost2 = postRepository.countFilesByPostId(2L);
        assertEquals(0, filesQuantityPost2);
    }
}