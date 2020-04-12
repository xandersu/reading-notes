package com.xandersu.readingnotes.imooc.class132_google_interview_source_code;

/**
 * @author: suxun
 * @date: 2020/4/12 17:26
 * @description:
 */
public class BinarySearchUtil {

    public static Integer binarySearch(Integer[] arr, int k) {
        if (arr == null || arr.length == 0) {
            return null;
        }
        int start = 0;
        int end = arr.length - 1;
        int middle = (start + end) / 2;

        while (start < end && middle != start && middle != end && middle < arr.length - 1) {
            int startValue = arr[start];
            if (startValue == k) {
                return start;
            }
            int endValue = arr[end];
            if (endValue == k) {
                return end;
            }

            int middleValue = arr[middle];

            if (middleValue < k) {
                start = middle;
                middle = (start + end) / 2;
            } else if (middleValue > k) {
                end = middle;
                middle = (start + end) / 2;
            } else {
                return middle;
            }
        }
        return null;
    }

    public static Integer binarySearch2(Integer[] arr, int k) {
        if (arr == null || arr.length == 0) {
            return null;
        }
        int start = 0;
        int end = arr.length;

        while (start < end) {
            int middle = (start + end) / 2;
            int middleValue = arr[middle];

            if (middleValue < k) {
                start = middle + 1;
            } else if (middleValue > k) {
                end = middle;
            } else {
                return middle;
            }
        }
        return null;
    }

}
