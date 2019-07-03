package cn.swiftdev.example.framework.core;

public interface LLBeanFactory {
    Object getBean(String beanName) throws Exception;

    Object getBean(Class<?> beanClass) throws Exception;
}
