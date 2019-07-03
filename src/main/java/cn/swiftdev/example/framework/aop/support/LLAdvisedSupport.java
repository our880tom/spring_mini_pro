package cn.swiftdev.example.framework.aop.support;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.swiftdev.example.framework.aop.aspect.LLAfterThrowingAdviceInterceptor;
import cn.swiftdev.example.framework.aop.aspect.LLMethodBeforeAdviceInterceptor;
import cn.swiftdev.example.framework.aop.config.LLAopConfig;

public class LLAdvisedSupport {

    private Class<?> targetClass;

    private Object target;

    private LLAopConfig config;

    private Pattern pointCutClassPattern;

    private transient Map<Method, List<Object>> methodCache;

    public LLAdvisedSupport(LLAopConfig config){
        this.config = config;
    }

    public Class<?> getTargetClass(){
        return this.targetClass;
    }

    public Object getTarget(){
        return this.target;
    }

    public List<Object> getInterceptorAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws Exception{
        List<Object> cached = methodCache.get(method);
        if (cached == null){
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cached = methodCache.get(m);
            this.methodCache.put(m, cached);
        }

        return cached;
    }

    public void setTargetClass(Class<?> targetClass){
        this.targetClass = targetClass;
        parse();
    }

    private void parse() {
        String pointCut = config.getPointCut()
                .replaceAll("\\.","\\\\.")
                .replaceAll("\\\\.\\*",".*")
                .replaceAll("\\(","\\\\(")
                .replaceAll("\\)","\\\\)");
        String pointCutForClassRegex = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(pointCutForClassRegex.lastIndexOf(" ") + 1));

        try {
            methodCache = new HashMap<>();
            Pattern pattern = Pattern.compile(pointCut);

            Class aspectClass = Class.forName(this.config.getAspectClass());
            Map<String, Method> aspectMethods = new HashMap<>();
            for (Method m : aspectClass.getMethods()){
                aspectMethods.put(m.getName(), m);
            }

            for (Method m: this.targetClass.getMethods()){
                String methodString = m.toString();
                if (methodString.contains("throws")){
                    methodString = methodString.substring(0, methodString.lastIndexOf("throw")).trim();
                }

                Matcher matcher = pattern.matcher(methodString);

                if (matcher.matches()){
                    List<Object> advices = new LinkedList<>();
                    if (!(config.getAspectBefore() == null || "".equals(config.getAspectBefore()))){
                        advices.add(new LLMethodBeforeAdviceInterceptor(aspectMethods.get(config.getAspectBefore()),aspectClass.newInstance()));
                    }

                    if (!(config.getAspectAfter() == null || "".equals(config.getAspectAfter()))){
                        advices.add(new LLMethodBeforeAdviceInterceptor(aspectMethods.get(config.getAspectAfter()),aspectClass.newInstance()));
                    }

                    if (!(config.getAspectAfterThrow() == null || "".equals(config.getAspectAfterThrow()))){
                        LLAfterThrowingAdviceInterceptor throwingAdviceInterceptor = new LLAfterThrowingAdviceInterceptor(
                                aspectMethods.get(config.getAspectAfterThrow()), aspectClass.newInstance()
                        );
                        throwingAdviceInterceptor.setThrowingName(config.getAspectAfterThrowingName());
                    }
                    methodCache.put(m, advices);

                }
            }


        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void setTarget(Object target){
        this.target = target;
    }

    public boolean pointCutMatch(){
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }

}
