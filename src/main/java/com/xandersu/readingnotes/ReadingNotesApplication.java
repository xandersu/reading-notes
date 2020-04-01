package com.xandersu.readingnotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@MapperScan("com.xandersu.readingnotes.imooc.class404_spring_source_code.mybatis.mapper")
@SpringBootApplication
//@PropertySource({"demo.properties"})
public class ReadingNotesApplication {

//    @Autowired
//    private BException bException;

    public static void main(String[] args) {
//        SpringApplication.run(ReadingNotesApplication.class, args);
        SpringApplication springApplication = new SpringApplication(ReadingNotesApplication.class);
//        Properties properties = new Properties();
//        properties.setProperty("test.test", "666");
//        springApplication.setDefaultProperties(properties);
        springApplication.run(args);
    }

}
