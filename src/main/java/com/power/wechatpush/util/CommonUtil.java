package com.power.wechatpush.util;

import java.util.Random;

public class CommonUtil {


    public static String getBatchNo() {
        long timeMillis = System.currentTimeMillis();
        int i = new Random().nextInt(10000);
        return String.format("%s-%s", timeMillis, i);
    }

}
