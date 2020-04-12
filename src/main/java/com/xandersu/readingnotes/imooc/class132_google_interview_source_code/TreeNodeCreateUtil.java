package com.xandersu.readingnotes.imooc.class132_google_interview_source_code;

/**
 * @author: suxun
 * @date: 2020/4/12 17:46
 * @description:
 */
public class TreeNodeCreateUtil {

    public static TreeNode create() {
        TreeNode root = new TreeNode('A');
        root.setLeft(new TreeNode('B'));
        root.setRight(new TreeNode('C'));

        root.getLeft().setLeft(new TreeNode('D'));
        root.getLeft().setRight(new TreeNode('E'));

        root.getRight().setRight(new TreeNode('F'));

        root.getLeft().getRight().setLeft(new TreeNode('G'));

        return root;
    }

    public static TreeNode create(String preOrder, String inOrder) {
        if (preOrder.isEmpty()) {
            return null;
        }

        char rootValue = preOrder.charAt(0);
        int rootIndex = inOrder.indexOf(rootValue);

        TreeNode root = new TreeNode(rootValue);
        root.setLeft(create(preOrder.substring(1, 1 + rootIndex), inOrder.substring(0, rootIndex)));
        root.setRight(create(preOrder.substring(1 + rootIndex), inOrder.substring(1 + rootIndex)));

        return root;
    }

    public static String postOrder(String preOrder, String inOrder) {
        if (preOrder.isEmpty()) {
            return "";
        }
        char rootValue = preOrder.charAt(0);
        int rootIndex = inOrder.indexOf(rootValue);

        return postOrder(preOrder.substring(1, 1 + rootIndex), inOrder.substring(0, rootIndex))
                + postOrder(preOrder.substring(1 + rootIndex), inOrder.substring(1 + rootIndex))
                + rootValue;
    }


    public static void main(String[] args) {

        TreeNodeTraversal.postOrder(create("ABDEGCF", "DBGEACF"));
        System.out.println("==============");
//        TreeNodeTraversal.postOrder(create("", ""));
//        System.out.println("==============");
//        TreeNodeTraversal.postOrder(create("AB", "BA"));
//        System.out.println("==============");
        System.out.println(postOrder("ABDEGCF", "DBGEACF"));
        System.out.println("==============");

    }

}
