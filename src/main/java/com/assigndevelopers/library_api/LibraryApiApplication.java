package com.assigndevelopers.library_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibraryApiApplication /*extends SpringBootServletInitializer*/ {

    // Extend your main class with SpringBootServletInitializer and override
    //its configure method
    /* *@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return super.configure(builder);
    }*/

    public static void main(String[] args) {
        SpringApplication.run(LibraryApiApplication.class, args);
    }

}
