package by.jdeveloper.myblogbackspringbootapp.mapper;


import by.jdeveloper.myblogbackspringbootapp.dto.NewPostDto;
import by.jdeveloper.myblogbackspringbootapp.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "likesCount", constant = "0L")
    @Mapping(target = "commentsCount", constant = "0L")
    Post toEntity(NewPostDto newPostDto);
}
