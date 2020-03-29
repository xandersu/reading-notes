package com.xandersu.readingnotes;

import com.xandersu.readingnotes.imooc.class404_spring_source_code.conditional.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ReadingNotesApplication.class})
public class ReadingNotesApplicationTests2 implements ApplicationContextAware {


    private ApplicationContext applicationContext;

    @Test
    public void contextLoads() {
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Test
    public void testA(){
        System.out.println(applicationContext.getBean(A.class));
    }

}
