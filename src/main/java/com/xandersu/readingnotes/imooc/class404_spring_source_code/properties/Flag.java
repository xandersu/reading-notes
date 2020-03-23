package com.xandersu.readingnotes.imooc.class404_spring_source_code.properties;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author: suxun
 * @date: 2020/3/23 21:29
 * @description:
 */
@Data
@Component
public class Flag {

    private boolean canOperate = true;

}
