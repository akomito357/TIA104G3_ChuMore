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

    public static List<String> convertStrToHours(String str) {
        List<Integer> timeList = convertStrToTimeList(str, 1);
        List<String> businessHours = new ArrayList<>();
        int start = -1; // 用來記錄區間起始時間
        int end;

        for (int i = 0; i < timeList.size(); i++) {
            if (timeList.get(i) == 1 && start == -1) { // 找到區間起點
                start = i;
            } else if ((timeList.get(i) == 0 || i == timeList.size() - 1) && start != -1) {
                // 找到區間終點或到達最後一個元素
                end = (timeList.get(i) == 0) ? i : i + 1; // 如果是最後一個元素，end應該包含最後時間
                String section = String.format("%02d:%02d-%02d:%02d", start, 0, end, 0);
                businessHours.add(section);
                start = -1; // 重置start，準備下一段區間
            }
        }
        return businessHours;
    }
}
