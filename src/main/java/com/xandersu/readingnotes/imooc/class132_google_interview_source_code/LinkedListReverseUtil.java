package com.xandersu.readingnotes.imooc.class132_google_interview_source_code;

/**
 * @author: suxun
 * @date: 2020/4/12 17:25
 * @description:
 */
public class LinkedListReverseUtil {

    //递归
    public static LinkedNode reverseLinkedList(LinkedNode head) {
//        if (head == null || head.getNext() == null) {
//            return head;
//        }
        if (head == null) {
            return null;
        }
        if (head.getNext() == null) {
            return head;
        }
        LinkedNode newHead = reverseLinkedList(head.getNext());
        head.getNext().setNext(head);
        head.setNext(null);
        return newHead;
    }

    //循环
    //newHead 指向翻转成功的链表
    //currentHead 指向还没有翻转的链表
    public LinkedNode reverseLinkedList2(LinkedNode head) {
        LinkedNode newHead = null;
        LinkedNode currentHead = head;

        while (currentHead != null) {
            LinkedNode next = currentHead.getNext();
            currentHead.setNext(newHead);
            newHead = currentHead;
            currentHead = next;
        }
        return newHead;
    }
}
