package by.jdeveloper.myblogbackspringbootapp.service;


import by.jdeveloper.myblogbackspringbootapp.dto.NewCommentDto;
import by.jdeveloper.myblogbackspringbootapp.dto.NewPostDto;
import by.jdeveloper.myblogbackspringbootapp.dto.PostUpdateDto;
import by.jdeveloper.myblogbackspringbootapp.dto.PostsResponse;
import by.jdeveloper.myblogbackspringbootapp.mapper.PostMapperImpl;
import by.jdeveloper.myblogbackspringbootapp.model.Comment;
import by.jdeveloper.myblogbackspringbootapp.model.Post;
import by.jdeveloper.myblogbackspringbootapp.repository.InnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {
        InnerRepository.class,
        PostMapperImpl.class,
        PostService.class
})
class PostServiceInnerIntegrationTests {

    @Autowired
    private InnerRepository innerRepository;

    @Autowired
    PostService postService;

    @BeforeEach
    void initRepository() {
        innerRepository.resetRepository();

        NewPostDto newPostDto = NewPostDto.builder()
                .title("zero title")
                .text("init text")
                .tags(List.of("zero_tag"))
                .build();

        postService.save(newPostDto);

        NewCommentDto newComment = NewCommentDto.builder()
                .postId(0L)
                .text("zeroth comment")
                .build();
        postService.saveComment(0L, newComment);
    }

    @Test
    void save() {
        NewPostDto newPostDto = NewPostDto.builder()
                .title("new title")
                .text("new text")
                .tags(List.of("new_tag"))
                .build();

        Post savedPost = postService.save(newPostDto);

        Post extractedPost = postService.findById(savedPost.getId());

        assertNotNull(extractedPost);
        assertEquals(1L, extractedPost.getId());
        assertEquals("new title", extractedPost.getTitle());
        assertEquals("new text", extractedPost.getText());
        assertEquals("new_tag", extractedPost.getTags().getFirst());
    }

    @Test
    void SaveComment() {
        NewCommentDto newCommentDto = NewCommentDto.builder()
                .postId(0L)
                .text("zeroth comment")
                .build();
        postService.saveComment(0L, newCommentDto);

        Comment comment = postService.getCommentsByPostIdAndCommentId("0", 0L);
        assertEquals(0L, comment.getPostId());
        assertEquals("zeroth comment", comment.getText());
    }

    @Test
    void deleteById() {
        postService.deleteById(0L);

        Post post = postService.findById(0L);
        assertNull(post);
    }

    @Test
    void update_post_when_not_found() {
        IllegalArgumentException illegalArgumentException = assertThrows(
                IllegalArgumentException.class,
                () -> postService.update(999L, new PostUpdateDto())
        );
        assertEquals("Post with id=999 not found", illegalArgumentException.getMessage(), "");
    }

    @Test
    void update_post() {
        PostUpdateDto postForUpdate = PostUpdateDto.builder()
                .title("title updated")
                .text("text updated")
                .tags(List.of("tag_updated"))
                .build();

        Post updated = postService.update(0L, postForUpdate);

        assertEquals(0L, updated.getId());
        assertEquals("title updated", updated.getTitle());
        assertEquals("text updated", updated.getText());
        assertEquals("tag_updated", updated.getTags().getFirst());
        assertEquals(1, updated.getTags().size());
    }

    @Test
    void findPosts() {
        PostsResponse posts = postService.findPosts("", 1, 10);

        assertEquals(1, posts.getPosts().size());
        assertEquals("zero title", posts.getPosts().getFirst().getTitle());
    }

    @Test
    void findUnexistedPosts() {
        PostsResponse posts = postService.findPosts("unexisted", 1, 10);

        assertEquals(0, posts.getPosts().size());
    }

    @Test
    void findPosts_when_tag_sended() {
        PostsResponse posts = postService.findPosts("#zero_tag", 1, 10);
        assertEquals(1, posts.getPosts().size());
        assertEquals("zero title", posts.getPosts().getFirst().getTitle());
    }

    @Test
    void findPosts_when_unexisted_tag_sended() {
        PostsResponse posts = postService.findPosts("#unexisted", 1, 10);
        assertEquals(0, posts.getPosts().size());
    }

    @Test
    void incrementLike() {
        Long incrementedLike = postService.incrementLike(0L);

        Post post = postService.findById(0L);
        assertEquals(1, incrementedLike);
        assertEquals(1, post.getLikesCount());

        for (int i = 0; i < 10; i++) {
            postService.incrementLike(0L);
        }
        assertEquals(11, post.getLikesCount());
    }

    @Test
    void findById() {
        Post post = postService.findById(0L);

        assertNotNull(post);
        assertEquals(0L, post.getId());
        assertEquals("zero title", post.getTitle());
        assertEquals("init text", post.getText());
        assertEquals("zero_tag", post.getTags().getFirst());
    }

    @Test
    void findById_empty() {
        Post post = postService.findById(999L);
        assertNull(post);
    }

    @Test
    void getCommentsByPostId() {
        List<Comment> comments = postService.getCommentsByPostId("0");

        assertEquals(1, comments.size());
        assertEquals("zeroth comment", comments.getFirst().getText());
        assertEquals(0L, comments.getFirst().getPostId());
        assertEquals(0L, comments.getFirst().getId());
    }

    @Test
    void getCommentsByPostId_InvalidPost() {
        IllegalArgumentException illegalArgumentException = assertThrows(
                IllegalArgumentException.class,
                () -> postService.getCommentsByPostId("X")
        );
        assertEquals("Invalid postId!", illegalArgumentException.getMessage());
    }

    @Test
    void getCommentsByPostIdAndCommentId() {
        Comment comment = postService.getCommentsByPostIdAndCommentId("0", 0L);

        assertEquals("zeroth comment", comment.getText());
        assertEquals(0L, comment.getPostId());
        assertEquals(0L, comment.getId());
    }


    @Test
    void getCommentsByPostIdAndCommentId_InvalidPost() {
        IllegalArgumentException illegalArgumentException = assertThrows(
                IllegalArgumentException.class,
                () -> postService.getCommentsByPostIdAndCommentId("X", 1L)
        );
        assertEquals("Invalid postId!", illegalArgumentException.getMessage());
    }

    @Test
    void deleteByPostIdAndCommentId() {
        postService.deleteByPostIdAndCommentId(0L, 0L);

        List<Comment> comments = postService.getCommentsByPostId("0");
        assertEquals(0, comments.size());
    }
}