package by.jdeveloper.myblogbackspringbootapp.dto;

import by.jdeveloper.myblogbackspringbootapp.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostsResponse {
    @Builder.Default
    private List<Post> posts = new ArrayList<>();
    private boolean hasPrev;
    private boolean hasNext;
    private int lastPage;

}
