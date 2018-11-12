package com.agile.common.util;

import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.*;

/**
 * Created by mydeathtrial on 2017/3/11
 */
public class PropertiesUtil {

    private static final String classPath = "/com/agile/conf/";
    public static Properties properties = new Properties();

    private PropertiesUtil(File file) {
        try {
            properties = new Properties();
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            properties.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PropertiesUtil(InputStream in){
        try {
            properties = new Properties();
            properties.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readDir(String classPathResource){
        try {
            File dir = new File(PropertiesUtil.class.getResource(classPathResource).toURI().getPath());
            if(dir.exists() && dir.isDirectory()){
                File[] files = dir.listFiles();
                for (File file:files) {
                    if(file.isFile()){
                        String fileName = file.getName();
                        if(fileName.endsWith("properties")){
                            mergeProperties(file);
                        }else if(fileName.endsWith("yml")){
                            mergeYml(classPathResource + file.getName());
                        }else if(fileName.endsWith("json")){
                            mergeJson(file);
                        }else if(fileName.endsWith("py") || fileName.endsWith("jrxml") || fileName.endsWith("datx")){
                            mergeFilePath(file);
                        }
                    }else if(file.isDirectory()){
                        readDir(classPathResource + file.getName() + "/");
                    }
                }
            }
        }catch (Exception ignored){}
    }

    static {
        readDir(classPath);
        merge(classPath + "agile.properties");
        merge(classPath + "agile.yml");
        merge(classPath + "application.properties");
        merge(classPath + "application.yml");
        merge(classPath + "bootstrap.properties");
        merge(classPath + "bootstrap.yml");
        merge(classPath + "agile-reset.properties");
        merge(classPath + "agile-reset.yml");
    }

    public static void merge(String fileName){
        try {

            if(ObjectUtil.isEmpty(fileName))return;
            if(fileName.endsWith("properties")){
                merge(PropertiesUtil.class.getResourceAsStream(fileName));
            }else if(fileName.endsWith("yml")){
                mergeYml(fileName);
            }else if(fileName.endsWith("json")){
                mergeJson(fileName,PropertiesUtil.class.getResourceAsStream(fileName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void merge(InputStream in){
        try {
            if(ObjectUtil.isEmpty(in))return;
            properties.load(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void merge(Properties p){
        for (Map.Entry<Object, Object> entry: p.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            properties.setProperty(key.toString(), value.toString());
        }
    }

    public static void mergeProperties(File file){
        try {
            if(ObjectUtil.isEmpty(file) || !file.getName().endsWith("properties"))return;
            merge(new BufferedInputStream(new FileInputStream(file)));
        }catch (Exception ignored){}
    }

    public static void mergeYml(String classPathResource){
        try {
            if(classPathResource == null || !classPathResource.endsWith("yml"))return;
            YamlPropertiesFactoryBean yml = new YamlPropertiesFactoryBean();
            ClassPathResource resource = new ClassPathResource(classPathResource);
            if(!resource.exists())return;
            yml.setResources(resource);
            merge(Objects.requireNonNull(yml.getObject()));
        }catch (Exception ignored){}
    }

    public static void mergeJson(File file){
        try {
            if(ObjectUtil.isEmpty(file) || !file.getName().endsWith("json"))return;
            String data = FileUtils.readFileToString(file,"UTF-8");
            properties.put(file.getName(),JSONUtil.toJSON(data));
        }catch (Exception ignored){}
    }

    private static void mergeJson(String fileName, InputStream in) {
        try {
            String data = IOUtils.toString(in, "UTF-8");
            properties.put(fileName,JSONUtil.toJSON(data));
        }catch (Exception ignored){}
    }


    private static void mergeFilePath(File file) {
        try {
            properties.put(file.getName(),file.getPath());
        }catch (Exception ignored){}
    }

    public static Properties getPropertys(){
        return properties;
    }

    /**
     * 获取工程配置信息
     * @param key 句柄
     * @return 值
     */
    public static String getProperty(String key){
        return properties.getProperty(key);
    }

    /**
     * 获取工程格式化响应文
     * @param key 句柄
     * @param params 占位参数
     * @return 值
     */
    public static String getMessage(String key,Object... params){
        Locale locale = null;
        try {
            locale = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest().getLocale();
        }catch (Exception ignored){}

        if(locale == null){
            locale = Locale.CHINA;
        }
        String message = null;
        try {
            message =  FactoryUtil.getBean(ResourceBundleMessageSource.class).getMessage(key,params, locale);
        }catch (Exception ignored){}

        if(message==null){
            try {
                message = MessageFormat.format(properties.getProperty(key),params);
            }catch (Exception ignored){}
        }

        return message;
    }

    /**
     * 根据文件名取classpath目录下的json数据
     * @param fileName 不带后缀文件名
     * @return JSONObject数据
     */
    public static JSONObject getJson(String fileName){
        String key = String.format("%s.json",fileName);
        return properties.get(key) instanceof JSONObject ? (JSONObject) properties.get(key) :null;
    }


    /**
     * 根据文件名取classpath目录下的json数据
     * @param fileName 不带后缀文件名
     * @return JSONObject数据
     */
    public static String getFilePath(String fileName){
        return getProperty(fileName);
    }

    /**
     * 从classpath目录下的所有json文件中，取出指定clazz类型对象数据集
     * @param clazz 指定对象类型
     * @param <T> 泛型
     * @return List泛型集合
     */
    public static <T>List<T> getObjectFromJson(Class<T> clazz){
        return getObjectFromJson(clazz,"");
    }

    /**
     * 从json数据中，取出指定clazz类型对象数据集
     * @param clazz 指定对象类型
     * @param <T> 泛型
     * @return List泛型集合
     */
    public static <T>List<T> getObjectFromJson(Class<T> clazz, JSONObject json){
        List<T> list = new ArrayList<>();
            try {
                list.add((T) JSONObject.toBean(json,clazz,getClassMap(clazz)));
            }catch (Exception ignored){
            }
        return list;
    }

    /**
     * 从classpath目录下，取指定文件后缀名位jsonFileName的json文件数据，获取其中的clazz对象集
     * @param clazz 指定对象类型
     * @param fileSuffixName json文件后缀名
     * @param <T> 泛型
     * @return List泛型集合
     */
    public static <T>List<T> getObjectFromJson(Class<T> clazz, String fileSuffixName){
        List<T> list = new ArrayList<>();
        Enumeration<Object> it = properties.keys();
        while (it.hasMoreElements()){
            String key = String.valueOf(it.nextElement());
            if(key.endsWith(String.format("%s.json",fileSuffixName))){
                JSONObject json = getJson(key);
                try {
                    list.add((T) JSONObject.toBean(json,clazz,getClassMap(clazz)));
                }catch (Exception ignored){
                }
            }
        }
        return list;
    }

    /**
     * 取出类中包含的集合类型属性信息，用于jsonObject转javaBean使用
     * @param clazz 指定对象类型
     * @return Map<属性名，属性类型>
     */
    private static Map getClassMap(Class clazz){
        Map<String, Class<?>> map= new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field:fields){
            if(Iterable.class.isAssignableFrom(field.getType())){
                Type genericType = field.getGenericType();
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                if(ArrayUtil.isEmpty(typeArguments) || typeArguments.length>1)continue;
                map.put(field.getName(),(Class)typeArguments[0]);
                if(!ClassUtil.isCustomClass((Class)typeArguments[0]))
                map.putAll(getClassMap((Class)typeArguments[0]));
            }else if(!ClassUtil.isCustomClass(field.getType())){
                map.putAll(getClassMap(field.getType()));
            }
        }
        return map;
    }

    /**
     * 获取工程配置信息
     * @param key 句柄
     * @return 值
     */
    public static String getProperty(String key,String defaultValue){
        if(!properties.containsKey(key))return defaultValue;
        return getProperty(key);
    }

    public static Properties getPropertyByPrefix(String prefix){
        Properties r = new Properties();
        Set<String> propertyNames = properties.stringPropertyNames();
        for (String name :propertyNames){
            if(name.startsWith(prefix)){
                r.put(name,properties.get(name));
            }
        }
        return r;

    }

    public static <T> T getProperty(String var1, Class<T> var2){
        return ObjectUtil.cast(var2,getProperty(var1));
    }

    public static <T> T getProperty(String var1, Class<T> var2,String defaultValue){
        return ObjectUtil.cast(var2,getProperty(var1,defaultValue));
    }

}
