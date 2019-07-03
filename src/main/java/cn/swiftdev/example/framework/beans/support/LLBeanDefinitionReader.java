package cn.swiftdev.example.framework.beans.support;

import cn.swiftdev.example.framework.beans.config.LLBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class LLBeanDefinitionReader {

    private List<String> registyBeanClasses = new ArrayList<>();

    private Properties config = new Properties();

    private final String SCAN_PACKAGE = "scanPackage";

    public LLBeanDefinitionReader(String... locations) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));

        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        doScanner(config.getProperty(SCAN_PACKAGE));

    }

    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource(File.separator + scanPackage.replaceAll("\\.", File.separator));
        File classPath = new File(url.getFile());

        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().startsWith(".class")) {
                    continue;
                }

                String beanName = scanPackage + "." + file.getName().replace(".class", "");
                registyBeanClasses.add(beanName);
            }
        }
    }

    public List<LLBeanDefinition> loadBeanDefinitions() {
        List<LLBeanDefinition> result = new ArrayList<>();

        try {
            for (String className : registyBeanClasses) {
                Class<?> beanClass = Class.forName(className);
                if (beanClass.isInterface()) {
                    continue;
                }

                result.add(doCreateBeanDifinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));

                Class<?>[] interfaces = beanClass.getInterfaces();

                for (Class<?> i: interfaces){
                    result.add(doCreateBeanDifinition(i.getName(),beanClass.getName()));
                }

            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private LLBeanDefinition doCreateBeanDifinition(String factoryName,String className){
        LLBeanDefinition llBeanDefinition = new LLBeanDefinition();
        llBeanDefinition.setFactoryBeanName(factoryName);
        llBeanDefinition.setBeanClassName(className);
        return llBeanDefinition;
    }

    private String toLowerFirstCase(String str){
        char[] charArray = str.toCharArray();
        charArray[0] += 32;
        return String.valueOf(charArray);
    }

    public Properties getConfig(){
        return this.config;
    }
}
