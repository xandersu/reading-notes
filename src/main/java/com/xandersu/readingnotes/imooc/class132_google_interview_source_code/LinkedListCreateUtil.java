package com.xandersu.readingnotes.imooc.class132_google_interview_source_code;

import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author: suxun
 * @date: 2020/4/12 14:56
 * @description:
 */
public class LinkedListCreateUtil {

    public static LinkedNode createLinkedList(List<Integer> values) {
        if (CollectionUtils.isEmpty(values)) {
            return null;
        }
        LinkedNode firstNode = new LinkedNode(values.get(0));
        LinkedNode headOfSubLinkedList = createLinkedList(values.subList(1, values.size()));
        firstNode.setNext(headOfSubLinkedList);

        return firstNode;
    }









    public static void main(String[] args) {
        LinkedListCreateUtil createLinkedListUtil = new LinkedListCreateUtil();
//        Node linkedList1 = linkedList.createLinkedList(null);
//        Node.printLinkedList(linkedList1);
//
//        Node linkedList2 = linkedList.createLinkedList(new ArrayList<>());
//        Node.printLinkedList(linkedList2);
//
//        Node linkedList3 = linkedList.createLinkedList(Collections.singletonList(1));
//        Node.printLinkedList(linkedList3);
//
//        Node linkedList4 = linkedList.createLinkedList(Arrays.asList(2, 2, 2, 2, 2));
//        Node.printLinkedList(linkedList4);
//
//        Node linkedList5 = linkedList.reverseLinkedList(linkedList4);
//        Node.printLinkedList(linkedList5);
//        Node linkedList6 = linkedList.reverseLinkedList(linkedList4);
//        Node.printLinkedList(linkedList6);

//        combinations(Lists.newArrayList(), Arrays.asList(1, 2, 3, 4), 2);
//        System.out.println("=======");
//        combinations(Lists.newArrayList(), Collections.emptyList(), 2);
//        System.out.println("=======");
//        combinations(Lists.newArrayList(), Collections.emptyList(), 0);
//        System.out.println("=======");
//        combinations(Lists.newArrayList(), Arrays.asList(1, 2, 3, 4, 5), 0);
//        System.out.println("=======");
//        combinations(Lists.newArrayList(), Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 4);

//        Node linkedList4Process = deleteIfEquals(linkedList4, 2);
//        Node.printLinkedList(linkedList4Process);

//        Integer[] a = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
//        Integer index1 = binarySearch2(a, 3);
//        System.out.println("k = " + index1);
//
//        Integer index2 = binarySearch2(a, 11);
//        System.out.println("k = " + index2);
//
//        Integer index3 = binarySearch2(a, 1);
//        System.out.println("k = " + index3);
//
//        Integer index4 = binarySearch2(a, 10);
//        System.out.println("k = " + index4);

    }
}
