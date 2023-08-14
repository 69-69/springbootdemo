package com.assigndevelopers.library_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class LibraryApiApplication /*extends SpringBootServletInitializer*/ {

    // Extend your main class with SpringBootServletInitializer and override
    //its configure method
    /*@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return super.configure(builder);
    }*/

    public static void main(String[] args) {
        SpringApplication.run(LibraryApiApplication.class, args);
    }

}
