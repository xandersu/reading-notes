package com.xandersu.readingnotes.imooc.class132_google_interview_source_code;

import lombok.Data;
import lombok.ToString;

/**
 * @author: suxun
 * @date: 2020/4/12 14:56
 * @description:
 */
@Data
@ToString
public class LinkedNode {
    private final Integer value;
    private LinkedNode next;

    public LinkedNode() {
        this.value = null;
        this.next = null;
    }

    public LinkedNode(Integer value) {
        this.value = value;
        this.next = null;
    }

    public static void printLinkedList(LinkedNode head) {
        while (head != null) {
            System.out.print(head.getValue());
            System.out.print(" => ");
            head = head.next;
        }
        System.out.println("end");
    }

}
