package cloud.agileframework.mvc.annotation;

import java.lang.reflect.Method;

/**
 * 描述：bean加载之后解析方法级的自定义注解解析器
 * <p>创建时间：2018/11/28<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
public interface ParsingMethodAfter extends Parsing {
    /**
     * 解析过程
     *
     * @param beanName beanName
     * @param method   method
     */
    void parsing(String beanName, Method method);
}
