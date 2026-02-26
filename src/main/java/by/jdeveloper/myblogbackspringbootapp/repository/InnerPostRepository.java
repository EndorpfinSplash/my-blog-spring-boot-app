package by.jdeveloper.myblogbackspringbootapp.repository;

import by.jdeveloper.myblogbackspringbootapp.dao.PostRepository;
import by.jdeveloper.myblogbackspringbootapp.dto.NewCommentDto;
import by.jdeveloper.myblogbackspringbootapp.model.Comment;
import by.jdeveloper.myblogbackspringbootapp.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InnerPostRepository implements PostRepository {
    static Long postCounter = 0L;
    static Long commentCounter = 0L;
    private final Map<Long, Post> postStorage = new HashMap<>();
    private final Map<Long, Map<Long, Comment>> commentStorage = new HashMap<>();
    private final Map<Long, Image> imageStorage = new HashMap<>();

    @Override
    public Collection<Post> findAllByTitleContains(String search) {
        return postStorage.values().stream()
                .filter(post -> post.getTitle().contains(search))
                .toList();
    }

    @Override
    public List<Post> getAll() {
        return postStorage.values()
                .stream()
                .toList();
    }

    @Override
    public Collection<Post> findAllByTagContains(String tag) {
        return postStorage.values().stream()
                .filter(post -> post.getTags().contains(tag))
                .toList();
    }

    @Override
    public Post save(Post post) {
        if (post != null) {
            post.setId(postCounter++);
            postStorage.put(post.getId(), post);
        }
        return post;
    }

    @Override
    public Post update(Long id, Post post) {
        post.setId(id);
        postStorage.put(id, post);
        return post;
    }

    @Override
    public Comment updateComment(Long commentId, Comment newComment) {
        Comment comment = commentStorage.get(newComment.getPostId()).get(commentId);
        comment.setText(newComment.getText());
        return comment;
    }


    @Override
    public Optional<Post> findById(Long id) {
        Post post = postStorage.get(id);
        return post == null ? Optional.empty() : Optional.of(post);
    }

    @Override
    public void deleteById(Long id) {
        postStorage.remove(id);
        commentStorage.remove(id);
        imageStorage.remove(id);
    }

    @Override
    public Long likesIncrease(Long postId) {
        Post post = postStorage.get(postId);
        Long likesCount = post.getLikesCount();
        post.setLikesCount(++likesCount);
        return likesCount;
    }

    @Override
    public List<Comment> findAllCommentsByPostId(Long postId) {
        return commentStorage.get(postId).values().stream().toList();
    }

    @Override
    public Comment save(Long postId, NewCommentDto newCommentDto) {
        Map<Long, Comment> commentMap = commentStorage.computeIfAbsent(postId, k ->new HashMap<>());
        Comment comment = Comment.builder()
                .id(commentCounter++)
                .postId(newCommentDto.getPostId())
                .text(newCommentDto.getText())
                .build();
        commentMap.put(comment.getId(), comment);
        return comment;
    }

    @Override
    public Comment findCommentByPostIdAndCommentId(Long postId, Long commentId) {
        return commentStorage.get(postId).get(commentId);
    }

    @Override
    public void deleteByPostIdAndCommentId(Long postId, Long commentId) {
        commentStorage.get(postId).remove(commentId);
    }

    @Override
    public void saveFile(Long postId, String name, byte[] data) {
        imageStorage.put(postId, new Image(name, data));
    }

    @Override
    public boolean updateFileByPostId(Long postId, String fileName, byte[] data) {
        if (imageStorage.containsKey(postId)) {
            imageStorage.put(postId, new Image(fileName, data));
            return true;
        }
        return false;
    }

    @Override
    public byte[] getFileByPostId(Long postId) {
        return imageStorage.getOrDefault(postId, new Image()).data;
    }

    @Override
    public Long countFilesByPostId(Long postId) {
        return imageStorage.containsKey(postId) ? 1L : 0L;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class Image {
        String fileName;
        @Builder.Default
        byte[] data = new byte[0];
    }

    public void resetRepository() {
        postCounter = 0L;
        commentCounter = 0L;
        this.postStorage.clear();
        this.commentStorage.clear();
        this.imageStorage.clear();
    }
}