package cn.swiftdev.example.framework.beans.support;

import cn.swiftdev.example.framework.beans.config.LLBeanDefinition;
import cn.swiftdev.example.framework.context.support.LLAbstarctApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LLDefaultListableBeanFactory extends LLAbstarctApplicationContext {
    protected final Map<String, LLBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);


    public static void main(String[] args) {

    }
}
