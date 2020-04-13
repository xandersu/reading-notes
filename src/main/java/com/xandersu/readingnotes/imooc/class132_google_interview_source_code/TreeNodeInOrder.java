package com.xandersu.readingnotes.imooc.class132_google_interview_source_code;

/**
 * @author: suxun
 * @date: 2020/4/13 21:22
 * @description:
 */
public class TreeNodeInOrder {

    public static TreeNode next(TreeNode node) {
        if (node == null) {
            return null;
        }
        if (node.getRight() != null) {
            return first(node.getRight());
        } else {
            while (node.getParent() != null && node.getParent().getLeft() != node) {
                node = node.getParent();
            }
            return node.getParent();
        }
    }

    public static TreeNode first(TreeNode node) {
        if (node == null) {
            return null;
        }
        TreeNode currentNode = node;
        while (currentNode.getLeft() != null) {
            currentNode = currentNode.getLeft();
        }
        return currentNode;
    }


    public static void main(String[] args) {

    }
}
