package cn.swiftdev.example.framework.aop.aspect;

import cn.swiftdev.example.framework.aop.inpercept.LLMethodInterceptor;
import cn.swiftdev.example.framework.aop.inpercept.LLMethodInvocation;

import java.lang.reflect.Method;

public class LLAfterReturningAdviceInterceptor extends LLAbstractAspectAdvice implements LLAdvice, LLMethodInterceptor {

    private LLJoinPoint joinPoint;

    public LLAfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(LLMethodInvocation invocation) throws Throwable {
        Object retVal = invocation.proceed();
        this.joinPoint = invocation;
        afterReturning(retVal, invocation.getMethod(), invocation.getArguments(), invocation.getThis());
        return retVal;
    }

    private void afterReturning(Object retVal, Method method, Object[] arguments, Object aThis) throws Throwable{
        super.invokeAdviceMethod(this.joinPoint, retVal, null);
    }
}
