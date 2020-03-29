package com.xandersu.readingnotes;

import com.xandersu.readingnotes.imooc.class404_spring_source_code.exception.BException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import java.util.Properties;

@SpringBootApplication
@PropertySource({"demo.properties"})
public class ReadingNotesApplication {

    @Autowired
    private BException bException;

    public static void main(String[] args) {
//        SpringApplication.run(ReadingNotesApplication.class, args);
        SpringApplication springApplication = new SpringApplication(ReadingNotesApplication.class);
        Properties properties = new Properties();
        properties.setProperty("test.test", "666");
        springApplication.setDefaultProperties(properties);
        springApplication.run(args);
    }

}
