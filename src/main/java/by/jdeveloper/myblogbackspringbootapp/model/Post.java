package by.jdeveloper.myblogbackspringbootapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    private Long id;
    private String title;
    private String text;
    private List<String> tags;
    @Builder.Default
    private Long likesCount = 0L;
    @Builder.Default
    private Long commentsCount = 0L;
}
