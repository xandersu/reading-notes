package com.xandersu.readingnotes;

import com.xandersu.readingnotes.imooc.class404_spring_source_code.IOC.xml.HelloService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(locations = "classpath:ioc/demo.xml")
public class ImoocClassTests {

    @Autowired
    private HelloService helloService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testHello() {
        System.out.println(helloService.hello());
    }

    @Test
    public void testAnimal() {
        System.out.println(helloService.helloAnimal());
    }

}
