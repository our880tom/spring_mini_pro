package cn.swiftdev.example.demo;

import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
//        Test test = new Test();
//        String basePath = test.getClass().getResource("/").toString();
//        System.out.println(basePath);
//        File file = new File(basePath)

       int[] arr = {10,5,3,8,7};

        Arrays.sort(arr);

        for (int i = 0; i < arr.length; i ++){
            System.out.print(" " + arr[i]);
        }

        System.out.println();
        int index = 5;

        while (index -- > 0){
            System.out.print(" " + arr[index]);
        }



    }
}
