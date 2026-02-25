//package by.jdeveloper.myblogbackspringbootapp.service;
//
//
//import by.jdeveloper.myblogbackspringbootapp.dao.PostRepository;
//import by.jdeveloper.myblogbackspringbootapp.dto.NewCommentDto;
//import by.jdeveloper.myblogbackspringbootapp.dto.NewPostDto;
//import by.jdeveloper.myblogbackspringbootapp.dto.PostUpdateDto;
//import by.jdeveloper.myblogbackspringbootapp.model.Comment;
//import by.jdeveloper.myblogbackspringbootapp.model.Post;
//import by.jdeveloper.myblogbackspringbootapp.repository.InnerPostRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@SpringJUnitConfig(InnerIntegrationTestConfig.class)
//@ActiveProfiles("integration-tests")
//class PostServiceInnerIntegrationTests {
//
//    @Autowired
//    PostRepository postRepository;
//
//    @Autowired
//    PostService postService;
//
//    @BeforeEach
//    void initRepository() {
//        InnerPostRepository innerRepo = (InnerPostRepository) postRepository;
//        innerRepo.resetRepository();
//
//        NewPostDto newPostDto = NewPostDto.builder()
//                .title("zero title")
//                .text("init text")
//                .tags(List.of("zero_tag"))
//                .build();
//
//        postService.save(newPostDto);
//    }
//
//    @Test
//    void save() {
//        NewPostDto newPostDto = NewPostDto.builder()
//                .title("new title")
//                .text("new text")
//                .tags(List.of("new_tag"))
//                .build();
//
//        Post savedPost = postService.save(newPostDto);
//
//        Post extractedPost = postService.findById(savedPost.getId());
//
//        assertNotNull(extractedPost);
//        assertEquals(1L, extractedPost.getId());
//        assertEquals("new title", extractedPost.getTitle());
//        assertEquals("new text", extractedPost.getText());
//        assertEquals("new_tag", extractedPost.getTags().getFirst());
//    }
//
//    @Test
//    void SaveComment() {
//        NewCommentDto newCommentDto = NewCommentDto.builder()
//                .postId(0L)
//                .text("zeroth comment")
//                .build();
//        postService.saveComment(0L, newCommentDto);
//
//        Comment comment = postService.getCommentsByPostIdAndCommentId("0", 0L);
//        assertEquals(0L, comment.getPostId());
//        assertEquals("zeroth comment", comment.getText());
//    }
//
//    @Test
//    void deleteById() {
//        postService.deleteById(0L);
//
//        Post post = postService.findById(0L);
//        assertNull(post);
//    }
//
//    @Test
//    void update_post_when_not_found() {
//        IllegalArgumentException illegalArgumentException = assertThrows(
//                IllegalArgumentException.class,
//                () -> postService.update(999L, new PostUpdateDto())
//        );
//        assertEquals("Post with id=999 not found", illegalArgumentException.getMessage(), "");
//    }
//
//    @Test
//    void update_post() {
//        PostUpdateDto postForUpdate = PostUpdateDto.builder()
//                .title("title updated")
//                .text("text updated")
//                .tags(List.of("tag_updated"))
//                .build();
//
//        Post updated = postService.update(0L, postForUpdate);
//
//        assertEquals(0L, updated.getId());
//        assertEquals("title updated", updated.getTitle());
//        assertEquals("text updated", updated.getText());
//        assertEquals("tag_updated", updated.getTags().getFirst());
//        assertEquals(1, updated.getTags().size());
//    }
//
//    @Test
//    void findPosts() {
//        postService.findPosts("", 1, 10);
//        verify(postRepository, times(1)).findAllByTitleContains("");
//    }
//
//    @Test
//    void findPosts_when_tag_sended() {
//        postService.findPosts("#tag", 1, 10);
//        verify(postRepository, times(1)).findAllByTagContains("tag");
//    }
//
//    @Test
//    void incrementLike() {
//        postService.incrementLike(1L);
//        verify(postRepository, times(1)).likesIncrease(1L);
//    }
//
//    @Test
//    void findById() {
//        postService.findById(1L);
//        verify(postRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    void findById_empty() {
//        when(postRepository.findById(1L)).thenReturn(Optional.empty());
//        postService.findById(1L);
//        verify(postRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    void getCommentsByPostId() {
//        postService.getCommentsByPostId("1");
//        verify(postRepository, times(1)).findAllCommentsByPostId(1L);
//    }
//
//    @Test
//    void getCommentsByPostId_InvalidPost() {
//        IllegalArgumentException illegalArgumentException = assertThrows(
//                IllegalArgumentException.class,
//                () -> postService.getCommentsByPostId("X")
//        );
//        assertEquals("Invalid postId!", illegalArgumentException.getMessage());
//        verify(postRepository, never()).findAllCommentsByPostId(any());
//    }
//
//    @Test
//    void getCommentsByPostIdAndCommentId() {
//        postService.getCommentsByPostIdAndCommentId("1", 1L);
//        verify(postRepository, times(1)).findCommentByPostIdAndCommentId(1L, 1L);
//    }
//
//    @Test
//    void getCommentsByPostIdAndCommentId_InvalidPost() {
//        IllegalArgumentException illegalArgumentException = assertThrows(
//                IllegalArgumentException.class,
//                () -> postService.getCommentsByPostIdAndCommentId("X", 1L)
//        );
//        assertEquals("Invalid postId!", illegalArgumentException.getMessage());
//        verify(postRepository, never()).findAllCommentsByPostId(any());
//    }
//
//    @Test
//    void deleteByPostIdAndCommentId() {
//        postService.deleteByPostIdAndCommentId(1L, 1L);
//        verify(postRepository, times(1)).deleteByPostIdAndCommentId(1L, 1L);
//    }
//}