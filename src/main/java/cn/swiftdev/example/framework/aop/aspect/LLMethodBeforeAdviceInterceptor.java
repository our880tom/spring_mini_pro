package cn.swiftdev.example.framework.aop.aspect;

import cn.swiftdev.example.framework.aop.inpercept.LLMethodInterceptor;
import cn.swiftdev.example.framework.aop.inpercept.LLMethodInvocation;

import java.lang.reflect.Method;

public class LLMethodBeforeAdviceInterceptor extends LLAbstractAspectAdvice implements LLAdvice,LLMethodInterceptor {

    private LLJoinPoint joinPoint;

    public LLMethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    private void before(Method method, Object[] args, Object target) throws Throwable{
        super.invokeAdviceMethod(this.joinPoint, null, null);
    }
    @Override
    public Object invoke(LLMethodInvocation invocation) throws Throwable {
        this.joinPoint = invocation;
        before(invocation.getMethod(), invocation.getArguments(), invocation.getThis());
        return invocation.proceed();
    }
}
