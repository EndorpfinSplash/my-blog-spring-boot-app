package by.jdeveloper.myblogbackspringbootapp.controller;


import by.jdeveloper.myblogbackspringbootapp.dto.NewCommentDto;
import by.jdeveloper.myblogbackspringbootapp.dto.NewPostDto;
import by.jdeveloper.myblogbackspringbootapp.dto.PostUpdateDto;
import by.jdeveloper.myblogbackspringbootapp.dto.PostsResponse;
import by.jdeveloper.myblogbackspringbootapp.model.Comment;
import by.jdeveloper.myblogbackspringbootapp.model.Post;
import by.jdeveloper.myblogbackspringbootapp.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class PostController {

    private final PostService service;

    @GetMapping
    public PostsResponse getPosts(@RequestParam("search") String search,
                                  @RequestParam("pageNumber") int pageNumber,
                                  @RequestParam("pageSize") int pageSize) {
        return service.findPosts(search, pageNumber, pageSize);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Post save(@RequestBody NewPostDto newPostDto) {
        return service.save(newPostDto);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Post findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        service.deleteById(id);
    }


    @DeleteMapping(value = "/{id}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long postId,
                       @PathVariable("commentId") Long commentId) {
        service.deleteByPostIdAndCommentId(postId, commentId);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Post update(@PathVariable(name = "id") Long id, @RequestBody PostUpdateDto postUpdated) {
        return service.update(id, postUpdated);
    }

    @PostMapping("/{id}/likes")
    public Long incrementLike(@PathVariable("id") Long id) {
        return service.incrementLike(id);
    }

    @GetMapping(value = "/{id}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Comment> getComments(@PathVariable("id") String postId) {
        return service.getCommentsByPostId(postId);
    }

    @GetMapping(value = "/{id}/comments/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Comment getComment(
            @PathVariable("id") String postId,
            @PathVariable("commentId") Long commentId) {
        return service.getCommentsByPostIdAndCommentId(postId, commentId);
    }

    @PostMapping(value = "/{id}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public Comment save(
            @PathVariable("id") Long postId,
            @RequestBody NewCommentDto newCommentDto) {
        return service.saveComment(postId, newCommentDto);
    }
}
