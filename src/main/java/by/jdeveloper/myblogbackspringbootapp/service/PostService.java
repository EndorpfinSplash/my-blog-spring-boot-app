package by.jdeveloper.myblogbackspringbootapp.service;


import by.jdeveloper.myblogbackspringbootapp.dao.CommentRepository;
import by.jdeveloper.myblogbackspringbootapp.dao.PostRepository;
import by.jdeveloper.myblogbackspringbootapp.dto.NewCommentDto;
import by.jdeveloper.myblogbackspringbootapp.dto.NewPostDto;
import by.jdeveloper.myblogbackspringbootapp.dto.PostUpdateDto;
import by.jdeveloper.myblogbackspringbootapp.dto.PostsResponse;
import by.jdeveloper.myblogbackspringbootapp.mapper.PostMapper;
import by.jdeveloper.myblogbackspringbootapp.model.Comment;
import by.jdeveloper.myblogbackspringbootapp.model.Post;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostMapper postMapper;

    public Post save(NewPostDto newPostDto) {
        Post post = postMapper.toEntity(newPostDto);
        return postRepository.save(post);
    }

    public Comment saveComment(Long postId, NewCommentDto newCommentDto) {
        return commentRepository.save(postId, newCommentDto);
    }

    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }

    public Post update(Long id, PostUpdateDto postUpdated) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post with id=" + id + " not found"));

        post.setTitle(postUpdated.getTitle());
        post.setText(postUpdated.getText());
        post.setTags(postUpdated.getTags());

        return postRepository.update(id, post);
    }

    public Comment updateComment(Long id, Long commentId, NewCommentDto  newComment) {
        Comment comment = commentRepository.findCommentByPostIdAndCommentId(id, commentId);

        comment.setText(newComment.getText());
        return commentRepository.updateComment(id, comment);
    }

    public PostsResponse findPosts(String search,
                                   int pageNumber,
                                   int pageSize) {
        String[] searchArray = search.split(" +");
        LinkedList<String> titleSearchList = new LinkedList<>();
        LinkedList<String> tagSearchList = new LinkedList<>();
        for (String searchWord : searchArray) {
            if (searchWord.startsWith("#") && searchWord.length() > 1) {
                tagSearchList.add(searchWord.substring(1));
                continue;
            }
            titleSearchList.add(searchWord);
        }

        ArrayList<Post> searchedPosts = new ArrayList<>();
        if (!tagSearchList.isEmpty()) {
            tagSearchList.forEach(tag -> searchedPosts.addAll(postRepository.findAllByTagContains(tag)));
        }

        if (tagSearchList.isEmpty() && !titleSearchList.isEmpty()) {
            String searchByTitleLine = String.join(" ", titleSearchList);
            searchedPosts.addAll(postRepository.findAllByTitleContains(searchByTitleLine));
        }

        if (searchedPosts.isEmpty()) {
            return new PostsResponse();
        }

        int lastPage = (int) Math.ceil((double) searchedPosts.size() / pageSize);
        int fromIndex = (pageNumber - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, searchedPosts.size());
        List<Post> postsSlice = searchedPosts.subList(fromIndex, toIndex);
        return PostsResponse.builder()
                .posts(postsSlice)
                .hasNext(pageNumber < lastPage)
                .hasPrev(pageNumber > 1)
                .lastPage(lastPage)
                .build();
    }

    public Long incrementLike(Long postId) {
        return postRepository.likesIncrease(postId);
    }

    public Post findById(Long id) {
        return postRepository.findById(id).isEmpty()
                ? null
                : postRepository.findById(id).get();
    }

    public List<Comment> getCommentsByPostId(String postId) {
        long postIdParsed = getPostIdParsed(postId);
        return commentRepository.findAllCommentsByPostId(postIdParsed);
    }

    public Comment getCommentsByPostIdAndCommentId(String postId, Long commentId) {
        long postIdParsed = getPostIdParsed(postId);
        return commentRepository.findCommentByPostIdAndCommentId(postIdParsed, commentId);
    }

    public void deleteByPostIdAndCommentId(Long postId, Long commentId) {
        postRepository.deleteByPostIdAndCommentId(postId, commentId);
    }

    private static long getPostIdParsed(String postId) {
        long postIdParsed;
        try {
            postIdParsed = Long.parseLong(postId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid postId!");
        }
        return postIdParsed;
    }
}
