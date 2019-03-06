package com.agile.common.util;

import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 佟盟 on 2017/7/13
 */
public class ArrayUtil extends ArrayUtils {
    /**
     * 数组累加
     */
    public static String[] add(String[] array, String str) {
        String[] s = new String[array.length + 1];
        int i = 0;
        while (i < array.length) {
            s[i] = array[i];
            i++;
        }
        s[array.length] = str;
        return s;
    }

    /**
     * 数组累加
     */
    public static Object[] addAll(Object[] array1, Object[]... array2) {
        Object[] temp = array1;
        for (Object[] array : array2) {
            temp = addAll(temp, array);
        }
        return temp;
    }

    public static Object getLast(Object[] array) {
        return array[array.length - 1];
    }

    /**
     * 数组转list
     *
     * @param array 数组
     */
    public static <T> List<T> asList(T... array) {
        return Arrays.asList(array);
    }

    public static <T> List<T> cast(Class<T> clazz, Object[] objects) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < objects.length; i++) {
            T node = ObjectUtil.cast(clazz, objects[i]);
            if (node == null) {
                continue;
            }
            result.add(node);
        }
        return result;
    }
}
