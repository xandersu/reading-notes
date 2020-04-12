package com.xandersu.readingnotes.imooc.class132_google_interview_source_code;

/**
 * @author: suxun
 * @date: 2020/4/12 17:26
 * @description:
 */
public class LinkedListDeleteIfEqualsUtil {

    public static LinkedNode deleteIfEquals(LinkedNode head, int value) {
        while (head != null && head.getValue() == value) {
            head = head.getNext();
        }

        if (head == null) {
            return null;
        }

        LinkedNode prev = head;
        while (prev.getNext() != null) {
            LinkedNode next = prev.getNext();
            if (next.getValue() == value) {
                next = next.getNext();
                prev.setNext(next);
            } else {
                prev = next;
            }
        }

        return head;
    }
}
