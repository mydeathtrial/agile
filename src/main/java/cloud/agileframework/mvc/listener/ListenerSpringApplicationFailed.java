package cloud.agileframework.mvc.listener;

import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author 佟盟
 * 日期 2020/1/8 16:35
 * 描述 工程启动失败监听
 * @version 1.0
 * @since 1.0
 */
public class ListenerSpringApplicationFailed implements ApplicationListener<ApplicationFailedEvent> {

    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
        ProjectContextHolder.setStatus(STATUS.ERROR);
        ProjectContextHolder.print();
        event.getException().printStackTrace();
    }
}
