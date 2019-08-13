package com.mceil.item.bo;

import java.util.HashSet;
import java.util.Set;

public class GoodsSnCreate {
    public static void main(String[] args) {
        System.out.println(getNumber());
    }
    /** 得到一个0-9的随机数 */
    private static int getRandomNumber() {
        return (int) ((Math.random() * 100) % 10);
    }
    /** 得到一个四位无重复数字的数 */
    public static int getNumber() {
        Set<Integer> set = new HashSet<Integer>();
        while (true) {
            int a = getRandomNumber();
            set.add(new Integer(a));//Set里面的元素是不重复的，如果重复是存不进去的。
            if(set.size()>3)
                break;
        }
        int index = (int) ((Math.random() * 100) % 4);
        if(index==0){index+=1;}
        Integer[] arr = new Integer[set.size()];
        set.toArray(arr);
        String s = "";
        if(arr[0].intValue()==0){//如果第一位是0，则随机和后面三位交换
            Integer temp = arr[0];
            arr[0] = arr[index];
            arr[index] = temp;
        }
        for(int i=0;i<arr.length;i++){
            s += arr[i].intValue();
        }
        return Integer.parseInt(s);
    }
}
