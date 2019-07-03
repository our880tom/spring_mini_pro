package cn.swiftdev.example.framework.beans.config;

public class LLBeanPostProcessor {
    public Object postProcessBeforeInitalization(Object bean, String beanName) throws Exception{
        return bean;
    }

    public Object postProcessAfterInitalization(Object bean, String beanName) throws Exception{
        return bean;
    }
}
