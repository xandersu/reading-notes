package com.xandersu.readingnotes;

import com.xandersu.readingnotes.imooc.class404_spring_source_code.mybatis.mapper.DemoMapper;
import com.xandersu.readingnotes.imooc.class404_spring_source_code.mybatis.model.Demo;
import com.xandersu.readingnotes.imooc.class404_spring_source_code.mybatis.model.DemoExample;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReadingNotesApplication.class)
public class MybatisTests {

    @Autowired
    private DemoMapper demoMapper;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testInsert() {
        Demo demo = new Demo();
        demo.setName("锅姐");
        demo.setJob("测开");
        demoMapper.insertSelective(demo);
    }

    @Test
    public void testUpdate() {
        Demo demo = new Demo();
        demo.setId(1L);
        demo.setName("锅姐2代");
//        demo.setJob("测开");
        demoMapper.updateByPrimaryKeySelective(demo);
    }

    @Test
    public void testQuery() {
        DemoExample demoExample = new DemoExample();
        demoExample.createCriteria().andNameLike("%锅姐%");
        List<Demo> demos = demoMapper.selectByExample(demoExample);
        log.info("demos = {}", StringUtil.join(" | ", demos));
    }

    @Test
    public void testDelete() {
        demoMapper.deleteByPrimaryKey(1L);
    }
}
