package cn.swiftdev.example.framework.context;

import cn.swiftdev.example.framework.annotation.LLController;
import cn.swiftdev.example.framework.aop.LLAopProxy;
import cn.swiftdev.example.framework.aop.LLCglibAopProxy;
import cn.swiftdev.example.framework.aop.LLJdkDynamicAopProxy;
import cn.swiftdev.example.framework.aop.config.LLAopConfig;
import cn.swiftdev.example.framework.aop.support.LLAdvisedSupport;
import cn.swiftdev.example.framework.beans.LLBeanWrapper;
import cn.swiftdev.example.framework.beans.config.LLBeanDefinition;
import cn.swiftdev.example.framework.beans.config.LLBeanPostProcessor;
import cn.swiftdev.example.framework.beans.support.LLBeanDefinitionReader;
import cn.swiftdev.example.framework.core.LLBeanFactory;
import cn.swiftdev.example.framework.beans.support.LLDefaultListableBeanFactory;
import cn.swiftdev.example.framework.annotation.LLAutowired;
import cn.swiftdev.example.framework.annotation.LLService;
import sun.net.ftp.FtpClient;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class LLApplicationContext extends LLDefaultListableBeanFactory implements LLBeanFactory {

    private String[] configLocations;

    private LLBeanDefinitionReader reader;

    private Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>();

    private Map<String, LLBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();

    public LLApplicationContext(String... configLocations) {
        this.configLocations = configLocations;

        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        LLBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);

        try {
            LLBeanPostProcessor beanPostProcessor = new LLBeanPostProcessor();
            Object instance = instantiateBean(beanDefinition);
            if (instance == null) {
                return null;
            }

            beanPostProcessor.postProcessBeforeInitalization(instance, beanName);
            LLBeanWrapper beanWrapper = new LLBeanWrapper(instance);
            factoryBeanInstanceCache.put(beanName, beanWrapper);
            beanPostProcessor.postProcessAfterInitalization(instance, beanName);

            populateBean(beanName, instance);

            return factoryBeanInstanceCache.get(beanName).getWrappedInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void populateBean(String beanName, Object instance) {
        Class<?> clazz = instance.getClass();

        if (!(clazz.isAnnotationPresent(LLController.class) || clazz.isAnnotationPresent(LLService.class))){
            return;
        }

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields){
            if (!field.isAnnotationPresent(LLAutowired.class)){
                continue;
            }

            LLAutowired autowired = field.getAnnotation(LLAutowired.class);
            String fieldBeanName = autowired.value().trim();
            if ("".equals(fieldBeanName)){
                fieldBeanName = field.getType().getName();
            }

            field.setAccessible(true);

            try {
                field.set(instance, this.factoryBeanInstanceCache.get(fieldBeanName));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    private Object instantiateBean(LLBeanDefinition beanDefinition) {

        Object instance = null;
        String className = beanDefinition.getBeanClassName();


        try {
            if (factoryBeanObjectCache.containsKey(className)) {
                instance = factoryBeanObjectCache.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                LLAdvisedSupport config = instantionAopConfig(beanDefinition);

                config.setTargetClass(clazz);
                config.setTarget(instance);

                if (config.pointCutMatch()){
                    instance = createProxy(config).getProxy();
                }

                factoryBeanObjectCache.put(className, instance);
                factoryBeanObjectCache.put(beanDefinition.getFactoryBeanName(), instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return instance;
    }

    private LLAopProxy createProxy(LLAdvisedSupport config) {
        Class<?> targetClass = config.getTargetClass();

        if (targetClass.getInterfaces().length > 0){
            return new LLJdkDynamicAopProxy(config);
        }

        return new LLCglibAopProxy(config);
    }

    private LLAdvisedSupport instantionAopConfig(LLBeanDefinition beanDefinition) {
        LLAopConfig config = new LLAopConfig();
        config.setPointCut(reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(reader.getConfig().getProperty("aspectAfterThrowName"));

        return new LLAdvisedSupport(config);
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    @Override
    public void refresh() throws Exception {
        System.out.println();
        //1.定位，定位配置文件
        reader = new LLBeanDefinitionReader(configLocations);
        //2.加载配置文件，扫描相关的类，把它们封装到BeanDefinetion
        List<LLBeanDefinition> beanDefinitions =  reader.loadBeanDefinitions();
        //3.注册，把配置信息放到容器里面
        doRegisterBeanDifinition(beanDefinitions);
        //4。把不是延时加载的bean进行加载
        doAutowirted();

    }

    private void doRegisterBeanDifinition(List<LLBeanDefinition> beanDefinitions) throws Exception{

        for (LLBeanDefinition beanDefinition : beanDefinitions){
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
                throw new Exception("class " + beanDefinition.getFactoryBeanName() + "is exits!");
            }

            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }

    private void doAutowirted(){
        for (Map.Entry<String, LLBeanDefinition> beanDefinition : beanDefinitionMap.entrySet()){
            String beanName = beanDefinition.getKey();

            if (!beanDefinition.getValue().isLazyInit()){
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String[] getBeanDefinitionNames(){
        return beanDefinitionMap.keySet().toArray(new String[beanDefinitionMap.size()]);
    }

    public Properties getConfig(){

        return reader.getConfig();
    }




}
