package by.jdeveloper.myblogbackspringbootapp.service;


import by.jdeveloper.myblogbackspringbootapp.dao.PostRepository;
import by.jdeveloper.myblogbackspringbootapp.mapper.PostMapper;
import by.jdeveloper.myblogbackspringbootapp.repository.InnerPostRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("integration-tests")
@ComponentScan("by.jdeveloper.myblogbackspringbootapp")
public class InnerIntegrationTestConfig {

    @Bean
    public PostRepository orderRepository() {
        return new InnerPostRepository();
    }

    @Bean
    public PostMapper postMapper() {
        return new PostMapperImpl();
    }

}
