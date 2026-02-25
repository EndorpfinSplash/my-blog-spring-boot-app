package by.jdeveloper.myblogbackspringbootapp.service;


import by.jdeveloper.myblogbackspringbootapp.dao.PostRepository;
import by.jdeveloper.myblogbackspringbootapp.mapper.PostMapper;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("unit-tests")
@ComponentScan("by.jdeveloper.myblogbackspringbootapp.service")
public class UnitTestConfig {

    @Bean
    @Primary
    public PostRepository mockOrderRepository() {
        return Mockito.mock(PostRepository.class);
    }

    @Bean
    @Primary
    public PostMapper mockPostMapper() {
        return Mockito.mock(PostMapper.class);
    }

}
