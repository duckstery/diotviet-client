package diotviet.server;

import diotviet.server.repositories.CategoryRepository;
import diotviet.server.repositories.GroupRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;

import java.awt.image.BufferedImage;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class ServerApplication {

    /**
     * Category repository
     */
    @Autowired
    CategoryRepository categoryRepository;
    /**
     * Group repository
     */
    @Autowired
    GroupRepository groupRepository;

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(){
        return args -> {
//            if (categoryRepository.count() <= 0) {
//                for(String name : new String[]{"Product", "Service", "Combo"})
//                {
//                    Category category = new Category();
//
//                    category.setName(name);
//                    category.setType(Type.PRODUCT);
//
//                    categoryRepository.save(category);
//                }
//            }
//
//            if (groupRepository.count() <= 0) {
//                for(String name : new String[]{"Group 1", "Group 2", "Group 3"})
//                {
//                    Group category = new Group();
//
//                    category.setName(name);
//                    category.setType(Type.PRODUCT);
//
//                    groupRepository.save(category);
//                }
//            }
        };
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        // Config
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
        return modelMapper;
    }

    @Bean
    public HttpMessageConverter<BufferedImage> createImageHttpMessageConverter() {
        return new BufferedImageHttpMessageConverter();
    }
}
