package by.jdeveloper.myblogbackspringbootapp.dao;

import by.jdeveloper.myblogbackspringbootapp.model.Post;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PostRepository {

    Collection<Post> findAllByTitleContains(String search);

    List<Post> getAll();

    Collection<Post> findAllByTagContains(String tag);

    Post save(Post post);

    Post update(Long id, Post post);

    Optional<Post> findById(Long id);

    void deleteById(Long id);

    Long likesIncrease(Long postId);

    void deleteByPostIdAndCommentId(Long postId, Long commentId);

}