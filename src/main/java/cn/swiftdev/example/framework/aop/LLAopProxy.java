package cn.swiftdev.example.framework.aop;

public interface LLAopProxy {

    Object getProxy();

    Object getProxy(ClassLoader classLoader);

}
