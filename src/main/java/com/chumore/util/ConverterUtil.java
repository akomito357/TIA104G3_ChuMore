package com.chumore.util;

import java.util.ArrayList;
import java.util.List;

public class ConverterUtil {
    public static List<Integer> convertStrToTimeList(String str, int chunkSize){
        List<Integer> timeList = new ArrayList<>();
        for(int i = 0;i < str.length();i+=chunkSize){
            timeList.add(Integer.parseInt(str.substring(i,i+chunkSize)));
        }
        return timeList;
    }


    public static String convertTimeListToStr(List<Integer> timeList,int chunkSize){
        StringBuilder sb = new StringBuilder();
        for(Integer num: timeList){
            if(chunkSize == 2) {
                sb.append(String.format("%02d", num));
            }else{
                sb.append(num);
            }
        }

        return sb.toString();
    }
}
