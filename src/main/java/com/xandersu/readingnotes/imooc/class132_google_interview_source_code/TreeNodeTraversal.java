package com.xandersu.readingnotes.imooc.class132_google_interview_source_code;

/**
 * @author: suxun
 * @date: 2020/4/12 17:49
 * @description:
 */
public class TreeNodeTraversal {

    public static void preOrder(TreeNode root) {
        if (root == null) {
            return;
        }
        System.out.println(root.getValue());
        preOrder(root.getLeft());
        preOrder(root.getRight());
    }

    public static void inOrder(TreeNode root) {
        if (root == null) {
            return;
        }
        inOrder(root.getLeft());
        System.out.println(root.getValue());
        inOrder(root.getRight());
    }

    public static void postOrder(TreeNode root) {
        if (root == null) {
            return;
        }
        postOrder(root.getLeft());
        postOrder(root.getRight());
        System.out.println(root.getValue());
    }


    public static void main(String[] args) {
        TreeNode treeNode = TreeNodeCreateUtil.create();

        preOrder(treeNode);
        System.out.println("===============");
        inOrder(treeNode);
        System.out.println("===============");
        postOrder(treeNode);
    }
}
