package com.xandersu.readingnotes.imooc.class404_spring_source_code.mybatis;

import com.xandersu.readingnotes.imooc.class404_spring_source_code.mybatis.model.Demo;
import org.springframework.stereotype.Component;

/**
 * @author: suxun
 * @date: 2020/4/1 21:01
 * @description:
 */
@Component
public class DemoRepository {

    public Demo getById(Long id) {
        Demo demo = new Demo();
        demo.setId(id);
        demo.setJob("job " + id);
        demo.setName("name " + id);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return demo;
    }
}
