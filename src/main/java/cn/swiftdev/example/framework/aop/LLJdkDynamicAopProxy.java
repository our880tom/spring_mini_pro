package cn.swiftdev.example.framework.aop;

import cn.swiftdev.example.framework.aop.inpercept.LLMethodInvocation;
import cn.swiftdev.example.framework.aop.support.LLAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class LLJdkDynamicAopProxy implements LLAopProxy, InvocationHandler {

    private LLAdvisedSupport advisedSupport;

    public LLJdkDynamicAopProxy(LLAdvisedSupport advisedSupport) {
        this.advisedSupport = advisedSupport;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.advisedSupport.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader, this.advisedSupport.getTargetClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> interceptorsAndDynamicMethodMatchers = this.advisedSupport.getInterceptorAndDynamicInterceptionAdvice(method, advisedSupport.getTargetClass());
        LLMethodInvocation invocation = new LLMethodInvocation(proxy,this.advisedSupport.getTarget(), method, args, this.advisedSupport.getTargetClass(), interceptorsAndDynamicMethodMatchers);
        return invocation.proceed();
    }
}
