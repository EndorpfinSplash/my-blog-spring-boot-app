package by.jdeveloper.myblogbackspringbootapp.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig(classes = {
        DataSourceConfiguration.class,
        WebConfiguration.class,
})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-application.properties")
class IntegrationTests {

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

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
    }

    @Test
    void getPosts_returnsJsonArray() throws Exception {
        mockMvc.perform(get("/api/posts?search=&pageNumber=1&pageSize=5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.posts", hasSize(2)))
                .andExpect(jsonPath("$.posts[0].title").value("Test title"))
                .andExpect(jsonPath("$.posts[1].title").value("TEST title 2"));
    }

    @Test
    void createPost_acceptsJson_andPersists() throws Exception {
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

        mockMvc.perform(get("/api/posts?search=&pageNumber=1&pageSize=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts", hasSize(3)));
    }

    @Test
    void deletePost_noContent() throws Exception {
        mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/posts?search=&pageNumber=1&pageSize=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts", hasSize(1)));
    }

    @Test
    void uploadAndDownloadImage_success() throws Exception {
        byte[] pngStub = new byte[]{(byte) 137, 80, 78, 71};
        MockMultipartFile file = new MockMultipartFile("image", "testImage.png", "image/png", pngStub);

        mockMvc.perform(
                        multipart("/api/posts/{id}/image", 1L)
                                .file(file)
                                .with(
                                        request -> {
                                            request.setMethod("PUT");
                                            return request;
                                        }
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(content().string("ok"));

        mockMvc.perform(get("/api/posts/{id}/image", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(content().bytes(pngStub));
    }

    @Test
    void uploadImage_emptyFile_badRequest() throws Exception {
        MockMultipartFile empty = new MockMultipartFile(
                "image",
                "empty.png",
                "image/png",
                new byte[0]
        );

        mockMvc.perform(
                        multipart("/api/posts/{id}/image", 1L)
                                .file(empty)
                                .with(
                                        request -> {
                                            request.setMethod("PUT");
                                            return request;
                                        }
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("empty file"));
    }

    @Test
    void uploadImage_userNotFound_404() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "image.png",
                "image/png",
                new byte[]{1, 2, 3}
        );

        mockMvc.perform(
                        multipart("/api/posts/{id}/image", 999L)
                                .file(file)
                                .with(
                                        request -> {
                                            request.setMethod("PUT");
                                            return request;
                                        }
                                )
                )
                .andExpect(status().isNotFound())
                .andExpect(content().string("Post not found"));
    }

    @Test
    void getImage_userHasNoImage_404() throws Exception {
        mockMvc.perform(get("/api/users/{id}/image", 2L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getImage_userNotFound_404() throws Exception {
        mockMvc.perform(get("/api/users/{id}/image", 777L))
                .andExpect(status().isNotFound());
    }
}
