package com.xandersu.readingnotes;

import com.xandersu.readingnotes.imooc.class404_spring_source_code.event.WeatherRunListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReadingNotesApplicationTests {


    @Autowired
    private WeatherRunListener weatherRunListener;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testEvent() {
        weatherRunListener.rain();
        weatherRunListener.snow();
    }

}
