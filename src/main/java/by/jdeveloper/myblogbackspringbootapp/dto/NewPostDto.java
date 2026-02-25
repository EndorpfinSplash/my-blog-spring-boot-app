package by.jdeveloper.myblogbackspringbootapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewPostDto {
    private String title;
    private String text;
    private List<String> tags;
}
