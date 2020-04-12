package com.xandersu.readingnotes.imooc.class132_google_interview_source_code;

import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author: suxun
 * @date: 2020/4/12 17:26
 * @description:
 */
public class CombinationsUtil {

    //combinations([1,2,3,4],2)
    //选1，combinations([2,3,4],1)
    //不选1，combinations([2,3,4],2)
    public static void combinations(List<Integer> selected, List<Integer> data, int n) {

        if (n == 0) {
            //返回空集
            for (Integer integer : selected) {
                System.out.print(integer);
                System.out.print(" ");
            }
            System.out.println();
            return;
        }

        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        if (n < 0) {
            return;
        }

        //选第0个元素
        selected.add(data.get(0));
        combinations(selected, data.subList(1, data.size()), n - 1);

        //不选第0个元素
        selected.remove(selected.size() - 1);
        combinations(selected, data.subList(1, data.size()), n);
    }
}
