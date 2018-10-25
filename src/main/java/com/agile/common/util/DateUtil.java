package com.agile.common.util;

import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 佟盟 on 2017/7/13
 */
public class DateUtil extends DateUtils {
    /**
     * 获取Long型时间戳
     */
    public static long getTimeStamp(){
        return getTimeStamp(new Date());
    }

    /**
     * 获取Long型时间戳
     */
    public static long getTimeStamp(Date date){
        return date.getTime();
    }

    /**
     * 获取时间戳字符串
     */
    public static String getTimeStampStr(){
        return getTimeStampStr(new Date());
    }

    /**
     * 获取时间戳字符串
     */
    public static String getTimeStampStr(Date date){
        return Long.toString(date.getTime());
    }

    /**
     * 获取时间戳字符串
     */
    public static Date getCurrentDate(){
        return new Date(System.currentTimeMillis());
    }

    /**
     * 字符串转日期
     * @param date 日期字符串
     * @param format 格式
     */
    public static Date toDateByFormat(String date,String format) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.parse(date);
    }

    public static String toFormatByDate(Date date,String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
}
