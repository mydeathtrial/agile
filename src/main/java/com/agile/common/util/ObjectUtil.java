package com.agile.common.util;

import org.springframework.util.ObjectUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;

/**
 * Created by 佟盟 on 2017/1/9
 */
public class ObjectUtil extends ObjectUtils {
    public enum ContainOrExclude{
        INCLUDE,EXCLUDE
    }

    /**
     * 复制对象中哪些属性
     * @param source 原对象
     * @param target 新对象
     * @param arguments 属性列表
     * @param containOrExclude 包含或排除
     */
    public static void copyProperties(Object source, Object target, String[] arguments, ContainOrExclude containOrExclude) {
        if (ObjectUtil.isEmpty(source) || ObjectUtil.isEmpty(target)) return;

        Field[] sourceFields = source.getClass().getDeclaredFields();
        for (int i = 0 ;i < sourceFields.length ; i++){
            String propertyName = sourceFields[i].getName();

            if(!ObjectUtil.isEmpty(arguments)){
                switch (containOrExclude){
                    case EXCLUDE:if (ArrayUtil.contains(arguments,propertyName)) continue;
                        break;
                    case INCLUDE:if (!ArrayUtil.contains(arguments,propertyName)) continue;
                        break;
                }
            }

            try {
                Field sourceProperty = source.getClass().getDeclaredField(propertyName);
                sourceProperty.setAccessible(true);
                Object value = sourceProperty.get(source);
                Field targetProperty = target.getClass().getDeclaredField(propertyName);
                targetProperty.setAccessible(true);
                targetProperty.set(target,value);
            }catch (Exception ignored){}
        }
    }

    /**
     * 复制对象属性
     * @param source 原对象
     * @param target 新对象
     */
    public static void copyProperties(Object source, Object target){
        copyProperties(source,target,null,null);
    }


    /**
     * 比较两个对象是否继承于同一个类
     * @param source 源对象
     * @param target 目标对象
     * @return 是否相同
     */
    public static Boolean compareClass(Object source,Object target){
        return isEmpty(source)?isEmpty(target):(!isEmpty(target) && source.getClass().equals(target.getClass()));
    }

    /**
     * 比较两个对象属性是否相同
     * @param source 源对象
     * @param target 目标对象
     * @return 是否相同
     */
    public static boolean compare(Object source, Object target) {
        return isEmpty(source)?isEmpty(target):(source.equals(target));
    }

    /**
     * 比较两个对象属性是否相同
     * @param source 源对象
     * @param target 目标对象
     * @return 是否相同
     */
    public static boolean compareValue(Object source, Object target) {
        if(isEmpty(source)){
            return isEmpty(target);
        }else{
            if(isEmpty(target))return false;
            try {
                List<Map<String, Object>> list = getDifferenceProperties(source, target);
                if(list != null && list.size()>0)return false;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 获取两个对象的不同属性列表
     * @param source 源对象
     * @param target 目标对象
     * @return 值不相同的属性列表
     * @throws IllegalAccessException 调用过程异常
     */
    public static List<Map<String,Object>> getDifferenceProperties(Object source,Object target) throws IllegalAccessException {
        if(((!compareClass(source, target) || compare(source, target)) || isEmpty(source)) != isEmpty(target))return null;
        List<Map<String,Object>> result = new ArrayList<>();
        Object sourceObject = isEmpty(source)?target:source;
        Object targetObject = isEmpty(source)?source:target;
        Class sourceClass = sourceObject.getClass();
        Field[] fields = sourceClass.getDeclaredFields();
        for(int i = 0 ; i < fields.length ; i++){
            Field field = fields[i];
            field.setAccessible(true);
            Object sourceValue = field.get(sourceObject);
            Object targetValue = field.get(targetObject);
            if (compare(sourceValue,targetValue)) {
                continue;
            }
            result.add(new HashMap<String,Object>() {
                private static final long serialVersionUID = -3959176970036247143L;
                {
                    put("propertyName", field.getName());
                    put("propertyType", field.getType());
                    put("oldValue", sourceValue);
                    put("newValue", targetValue);}
            });
        }
        return result;
    }

    /**
     * 从Map对象中获取指定类型对象
     * @param clazz 想要获取的对象类型
     * @param map 属性集合
     * @return 返回指定对象类型对象
     */
    public static  <T> T  getObjectFromMap(Class<T> clazz,Map<String, Object> map) {
        return getObjectFromMap(clazz,map,"","");
    }

    /**
     * 从Map对象中获取指定类型对象
     * @param clazz 想要获取的对象类型
     * @param map 属性集合
     * @return 返回指定对象类型对象
     */
    public static  <T> T  getObjectFromMap(Class<T> clazz,Map<String, Object> map, String prefix) {
        return getObjectFromMap(clazz,map,prefix,"");
    }

    /**
     * 从Map对象中获取指定类型对象
     * @param clazz 想要获取的对象类型
     * @param map 属性集合
     * @param prefix 属性前缀
     * @return 返回指定对象类型对象
     */
    public static <T> T getObjectFromMap(Class<T> clazz,Map<String, Object> map, String prefix, String suffix) {

        try {
            if(ObjectUtil.isEmpty(map))return null;
            T object = clazz.newInstance();
            boolean notNull = true;
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0 ; i < fields.length ; i++) {
                Field field = fields[i];
                String propertyName = prefix + field.getName() + suffix;
                if(map.containsKey(propertyName)){
                    try {
                        field.setAccessible(true);
                        Class<?> type = field.getType();
                        Object value = map.get(propertyName);
                        Object targetValue;
                        if(!type.isArray() && value.getClass().isArray()){
                            targetValue = cast(field.getType(), ((String[])value)[0]);
                        }else if(type.isArray() && value.getClass().isArray()){
                            targetValue = value;
                        }else{
                            targetValue = cast(type,value);
                        }
                        if(targetValue!=null){
                            if(notNull){
                                notNull = false;
                            }
                            field.set(object,targetValue);
                        }

                    }catch (Exception ignored){}

                }
            }
            if(notNull)return null;
            return object;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 判断对象非空属性是否存值（排除主键）
     */
    public static boolean isValidity(Object object){
        boolean result = true;
        if (object==null) return result;
        Class<?> clazz = object.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0 ; i < methods.length;i++){
            Method method = methods[i];
            if(!method.getName().startsWith("get"))continue;
            try {
                if(isEmpty(method.getAnnotation(Id.class))){
                    Column columInfo = method.getAnnotation(Column.class);
                    if(!isEmpty(columInfo) && !columInfo.nullable()){
                        if(isEmpty(method.invoke(object)))result = false;
                    }
                }
            }catch (Exception e){
                continue;
            }
        }
        return result;
    }

    public static boolean haveId(Field field,Object entity){
        try {
            Object id = field.get(entity);
            if(id==null || id.toString().trim().equals("")){
                return false;
            }
        } catch (IllegalAccessException e) {
            return false;
        }

        return true;
    }

    /**
     * 判断对象属性是否全空
     */
    public static boolean isAllNullValidity(Object object){
        boolean result = true;
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0 ; i < fields.length;i++){
            Field field = fields[i];
            field.setAccessible(true);
            try {
                if(!isEmpty(field.get(object)))result = false;
            }catch (Exception e){
                continue;
            }
        }
        return result;
    }

    /**
     * 对象类型转换
     * @param clazz 类型
     * @param value 值
     * @return 转换后的值
     */
    public static <T>T cast(Class<T> clazz, Object value) {
        if(ObjectUtil.isEmpty(value))return null;

        Object temp = null;
        String valueStr = String.valueOf(value);
        if(clazz == String.class){
            temp = valueStr;
        }
        if(clazz == Date.class){
            temp = Date.valueOf(valueStr);
        }
        if(clazz == Long.class|| clazz == long.class ){
            temp = Long.parseLong(valueStr);
        }
        if(clazz == Integer.class || clazz == int.class ){
            temp = Integer.parseInt(valueStr);
        }
        if(clazz == BigDecimal.class){
            temp = new BigDecimal(valueStr);
        }
        if(clazz == Double.class || clazz == double.class){
            temp = Double.parseDouble(valueStr);
        }
        if(clazz == Float.class || clazz == float.class){
            temp = Float.parseFloat(valueStr);
        }
        if(clazz == Boolean.class || clazz == boolean.class){
            temp = Boolean.parseBoolean(valueStr);
        }
        if(clazz == Byte.class || clazz == byte.class){
            temp = Byte.parseByte(valueStr);
        }
        if(clazz == Short.class || clazz == short.class){
            temp = Short.parseShort(valueStr);
        }
        if(clazz == Character.class || clazz == char.class){
            char[] array = valueStr.toCharArray();
            temp = array.length>0?array[0]:null;
        }

        return(T)(temp);
    }

    public static boolean compareOfNotNull(Object source,Object target){
        Field[] fields = source.getClass().getDeclaredFields();
        for (int i = 0 ; i < fields.length ; i++){
            Field field = fields[i];
            field.setAccessible(true);
            if("serialVersionUID".equals(field.getName()))continue;
            try {
                Object sourceValue = field.get(source);
                if(sourceValue==null)continue;
                Object targetValue = field.get(target);
                if(!sourceValue.equals(targetValue))return false;
            }catch (Exception e){
                continue;
            }
        }
        return true;
    }
}
