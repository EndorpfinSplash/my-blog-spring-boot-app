package by.jdeveloper.myblogbackspringbootapp.service;


import by.jdeveloper.myblogbackspringbootapp.dao.PostRepository;
import by.jdeveloper.myblogbackspringbootapp.dto.NewCommentDto;
import by.jdeveloper.myblogbackspringbootapp.dto.NewPostDto;
import by.jdeveloper.myblogbackspringbootapp.dto.PostUpdateDto;
import by.jdeveloper.myblogbackspringbootapp.mapper.PostMapper;
import by.jdeveloper.myblogbackspringbootapp.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(UnitTestConfig.class)
@ActiveProfiles("unit-tests")
class PostServiceUnitTests {

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostMapper postMapper;

    @Autowired
    PostService postService;

    @BeforeEach
    void resetMocks() {
        reset(postRepository);
        reset(postMapper);
    }

    @Test
    void save() {
        NewPostDto newPostDto = NewPostDto.builder()
                .title("new title")
                .text("new text")
                .tags(List.of("new_tag"))
                .build();

        Post mockPost = new Post();
        when(postMapper.toEntity(newPostDto)).thenReturn(mockPost);
        when(postRepository.save(any(Post.class))).thenReturn(mockPost);

        postService.save(newPostDto);
        verify(postMapper, times(1)).toEntity(any(NewPostDto.class));
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void SaveComment() {
        NewCommentDto newCommentDto = new NewCommentDto();
        postService.saveComment(1L, newCommentDto);
        verify(postRepository, times(1)).save(1L, newCommentDto);
    }

    @Test
    void deleteById() {
        postService.deleteById(1L);
        verify(postRepository, times(1)).deleteById(1L);
    }

    @Test
    void update_post_when_not_found() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());
        IllegalArgumentException illegalArgumentException = assertThrows(
                IllegalArgumentException.class,
                () -> postService.update(999L, new PostUpdateDto())
        );
        assertEquals("Post with id=999 not found", illegalArgumentException.getMessage(), "");
    }

    @Test
    void update_post() {
        Post post = new Post();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.update(anyLong(), any(Post.class))).thenReturn(post);

        postService.update(1L, new PostUpdateDto());

        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).update(anyLong(), any(Post.class));
    }

    @Test
    void findPosts() {
        postService.findPosts("", 1, 10);
        verify(postRepository, times(1)).findAllByTitleContains("");
    }

    @Test
    void findPosts_when_tag_sended() {
        postService.findPosts("#tag", 1, 10);
        verify(postRepository, times(1)).findAllByTagContains("tag");
    }

    @Test
    void incrementLike() {
        postService.incrementLike(1L);
        verify(postRepository, times(1)).likesIncrease(1L);
    }
    @Test
    void findById() {
        postService.findById(1L);
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void findById_empty() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        postService.findById(1L);
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void getCommentsByPostId() {
        postService.getCommentsByPostId("1");
        verify(postRepository, times(1)).findAllCommentsByPostId(1L);
    }

    @Test
    void getCommentsByPostId_InvalidPost() {
        IllegalArgumentException illegalArgumentException = assertThrows(
                IllegalArgumentException.class,
                () -> postService.getCommentsByPostId("X")
        );
        assertEquals("Invalid postId!", illegalArgumentException.getMessage());
        verify(postRepository, never()).findAllCommentsByPostId(any());
    }

    @Test
    void getCommentsByPostIdAndCommentId() {
        postService.getCommentsByPostIdAndCommentId("1", 1L);
        verify(postRepository, times(1)).findCommentByPostIdAndCommentId(1L, 1L);
    }

    @Test
    void getCommentsByPostIdAndCommentId_InvalidPost() {
        IllegalArgumentException illegalArgumentException = assertThrows(
                IllegalArgumentException.class,
                () -> postService.getCommentsByPostIdAndCommentId("X", 1L)
        );
        assertEquals("Invalid postId!", illegalArgumentException.getMessage());
        verify(postRepository, never()).findAllCommentsByPostId(any());
    }

    @Test
    void deleteByPostIdAndCommentId() {
        postService.deleteByPostIdAndCommentId(1L, 1L);
        verify(postRepository, times(1)).deleteByPostIdAndCommentId(1L, 1L);
    }
}