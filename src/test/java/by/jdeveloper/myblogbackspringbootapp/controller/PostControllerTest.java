package by.jdeveloper.myblogbackspringbootapp.controller;

import by.jdeveloper.myblogbackspringbootapp.dto.NewPostDto;
import by.jdeveloper.myblogbackspringbootapp.dto.PostsResponse;
import by.jdeveloper.myblogbackspringbootapp.model.Post;
import by.jdeveloper.myblogbackspringbootapp.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    private final Post post1 = Post.builder()
            .id(1L)
            .title("Test title")
            .text("test text")
            .tags(List.of("simple_tag"))
            .likesCount(0L)
            .build();

    private final Post post2 = Post.builder()
            .id(1L)
            .title("TEST title 2")
            .text("test second")
            .tags(List.of("test_second"))
            .likesCount(0L)
            .build();

    @Test
    void getPosts() throws Exception {
        when(postService.findPosts("", 1, 5))
                .thenReturn(new PostsResponse(List.of(post1, post2), false, false, 1));

        mockMvc.perform(get("/api/posts?search=&pageNumber=1&pageSize=5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.posts", hasSize(2)))
                .andExpect(jsonPath("$.posts[0].title").value("Test title"))
                .andExpect(jsonPath("$.posts[1].title").value("TEST title 2"));
    }

    @Test
    void save() throws Exception {

        NewPostDto postForSave = NewPostDto.builder()
                .title("Post for test")
                .text("this is integration test text")
                .tags(List.of("integration"))
                .build();
        Post savedPost = Post.builder()
                .id(3L)
                .title("Post for test")
                .text("this is integration test text")
                .tags(List.of("integration"))
                .build();
        when(postService.save(postForSave))
                .thenReturn(savedPost);

        String json = """
                  {
                  "title":"Post for test",
                  "text":"this is integration test text",
                  "tags":["integration"]
                  }
                """;

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.title").value("Post for test"))
                .andExpect(jsonPath("$.likesCount").value(0))
                .andExpect(jsonPath("$.tags[0]").value("integration"))
                .andExpect(jsonPath("$.text").value("this is integration test text"));
    }

    @Test
    void findById() {
    }

    @Test
    void delete() {
    }

    @Test
    void testDelete() {
    }

    @Test
    void update() {
    }

    @Test
    void incrementLike() {
    }

    @Test
    void getComments() {
    }

    @Test
    void getComment() {
    }

    @Test
    void testSave() {
    }

    @Test
    void updateComment() {
    }
}