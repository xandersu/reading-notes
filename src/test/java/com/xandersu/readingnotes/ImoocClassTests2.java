package com.xandersu.readingnotes;

import com.xandersu.readingnotes.imooc.class404_spring_source_code.IOC.ann.HelloService2;
import com.xandersu.readingnotes.imooc.class404_spring_source_code.IOC.ann.MyBeanImport;
import com.xandersu.readingnotes.imooc.class404_spring_source_code.IOC.ann.Teacher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@TestPropertySource({"demo.properties"})
@SpringBootTest(properties = {"test.test=8"})
@Import(MyBeanImport.class)
public class ImoocClassTests2 {

    @Autowired
    private HelloService2 helloService2;

    @Autowired
    private Teacher teacher;

    @Test
    public void contextLoads() {
    }


    @Test
    public void testHello2() {
        System.out.println(helloService2.hello());
    }

    @Test
    public void testTeacher() {
        System.out.println(teacher.getName());
    }
}
