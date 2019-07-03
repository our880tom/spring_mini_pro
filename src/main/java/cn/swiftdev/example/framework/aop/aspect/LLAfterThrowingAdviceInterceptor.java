package cn.swiftdev.example.framework.aop.aspect;

import cn.swiftdev.example.framework.aop.inpercept.LLMethodInterceptor;
import cn.swiftdev.example.framework.aop.inpercept.LLMethodInvocation;

import java.lang.reflect.Method;

public class LLAfterThrowingAdviceInterceptor extends LLAbstractAspectAdvice implements LLAdvice, LLMethodInterceptor {

    private String throwingName;

    public LLAfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }


    @Override
    public Object invoke(LLMethodInvocation invocation) throws Throwable {
        try{
            return invocation.proceed();
        } catch (Throwable e){
            invokeAdviceMethod(invocation, null, e.getCause());
            throw e;
        }

    }

    public void setThrowingName(String throwingName) {
        this.throwingName = throwingName;
    }
}
