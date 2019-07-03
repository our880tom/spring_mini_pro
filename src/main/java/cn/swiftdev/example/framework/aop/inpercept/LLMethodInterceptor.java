package cn.swiftdev.example.framework.aop.inpercept;

public interface LLMethodInterceptor {
    Object invoke(LLMethodInvocation invocation) throws Throwable;
}
