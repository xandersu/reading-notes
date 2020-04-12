package com.xandersu.readingnotes.imooc.class132_google_interview_source_code;

import lombok.Data;
import lombok.ToString;

/**
 * @author: suxun
 * @date: 2020/4/12 17:45
 * @description:
 */
@Data
@ToString
public class TreeNode {

    private char value;
    private TreeNode left;
    private TreeNode right;

    public TreeNode() {

    }

    public TreeNode(char value) {
        this.value = value;
    }

}
