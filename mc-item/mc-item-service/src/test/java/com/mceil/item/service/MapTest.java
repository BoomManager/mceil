package com.mceil.item.service;

import java.util.HashMap;
import java.util.Map;

public class MapTest {
    public static void main(String[] args) {
        try {
            Map<Integer,Long> map = new HashMap<>();
            map.put(1,2L);
            for (Map.Entry<Integer, Long> entry : map.entrySet()) {
                System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
