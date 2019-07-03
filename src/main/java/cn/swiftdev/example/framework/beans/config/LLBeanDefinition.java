package cn.swiftdev.example.framework.beans.config;


import lombok.Data;

@Data
public class LLBeanDefinition {
    private String beanClassName;

    private boolean lazyInit = false;

    private String factoryBeanName;
}
