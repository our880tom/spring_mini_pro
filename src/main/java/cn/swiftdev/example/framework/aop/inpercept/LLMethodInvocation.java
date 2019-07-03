package cn.swiftdev.example.framework.aop.inpercept;

import cn.swiftdev.example.framework.aop.aspect.LLJoinPoint;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LLMethodInvocation implements LLJoinPoint {

    private Object proxy;

    private Object target;

    private Method method;

    private  Object [] arguments;

    private Class<?> targetClass;

    private List<Object> interceptorsAndDynamicMethodMatchers;

    private Map<String, Object> userAttributes;

    private int currentInterceptorIndex = -1;

    public LLMethodInvocation(Object proxy, Object target, Method method, Object[] arguments, Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {
        this.proxy = proxy;
        this.target = target;
        this.method = method;
        this.arguments = arguments;
        this.targetClass = targetClass;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    public Object proceed() throws Throwable{
        if (currentInterceptorIndex == interceptorsAndDynamicMethodMatchers.size() - 1){
            return method.invoke(target, arguments);
        }

        Object interceptorOrInterceptionAdvice = this.interceptorsAndDynamicMethodMatchers.get(currentInterceptorIndex ++);
        if (interceptorOrInterceptionAdvice instanceof LLMethodInterceptor){
            LLMethodInterceptor interceptor = (LLMethodInterceptor)interceptorOrInterceptionAdvice;
            return interceptor.invoke(this);
        } else {
            return proceed();
        }

    }

    @Override
    public Object getThis() {
        return this.target;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public void setUserAttribute(String key, Object value) {
        if (value == null){
            if (this.userAttributes == null){
                this.userAttributes = new HashMap<>();
            }

            this.userAttributes.put(key, value);
        } else {
            if (this.userAttributes != null){
                this.userAttributes.remove(key);
            }
        }
    }

    @Override
    public Object getUserAttribute(String key) {
        return (this.userAttributes != null ? this.userAttributes.get(key) : null);
    }
}
