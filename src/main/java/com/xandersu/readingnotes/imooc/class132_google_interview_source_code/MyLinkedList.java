package com.xandersu.readingnotes.imooc.class132_google_interview_source_code;

import lombok.Data;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author: suxun
 * @date: 2020/4/13 22:17
 * @description:
 */
@Data
public class MyLinkedList implements Iterable<Integer> {

    private LinkedNode node;


    public static class MyI implements Iterator<Integer> {
        LinkedNode node;

        public MyI(LinkedNode node) {
            this.node = node;
        }

        @Override
        public boolean hasNext() {
            return node != null;
        }

        @Override
        public Integer next() {
            if (node == null) {
                throw new NoSuchElementException();
            }
            Integer value = node.getValue();
            node = node.getNext();
            return value;
        }
    }

    @Override
    public Iterator<Integer> iterator() {
        return new MyI(node);
    }

    public static void main(String[] args) {
        MyLinkedList linkedList = new MyLinkedList();
        LinkedNode head = new LinkedNode(0);
        linkedList.setNode(head);
        LinkedNode node = linkedList.getNode();

        for (int i = 1; i < 10; i++) {
            LinkedNode linkedNode = new LinkedNode(i);
            node.setNext(linkedNode);
            node = linkedNode;
        }

        for (Integer integer : linkedList) {
            System.out.println(integer);
        }

    }
}
