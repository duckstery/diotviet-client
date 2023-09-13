package diotviet.server;

import diotviet.server.entities.Customer;
import diotviet.server.entities.Product;
import diotviet.server.repositories.CategoryRepository;
import diotviet.server.repositories.GroupRepository;
import diotviet.server.templates.Customer.CustomerInteractRequest;
import diotviet.server.templates.Product.ProductInteractRequest;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;

import java.awt.image.BufferedImage;
import java.util.TimeZone;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableAsync
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
        // Set default TimeZone
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
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

        // Skip Group when map from ProductInteractRequest to Product
        modelMapper.addMappings(new PropertyMap<ProductInteractRequest, Product>() {
            @Override
            protected void configure() {
                skip(destination.getGroups());
            }
        });

        // Skip Group when map from CustomerInteractRequest to Customer
        modelMapper.addMappings(new PropertyMap<CustomerInteractRequest, Customer>() {
            @Override
            protected void configure() {
                skip(destination.getGroups());
            }
        });

        return modelMapper;
    }

    @Bean
    public HttpMessageConverter<BufferedImage> createImageHttpMessageConverter() {
        return new BufferedImageHttpMessageConverter();
    }
}
