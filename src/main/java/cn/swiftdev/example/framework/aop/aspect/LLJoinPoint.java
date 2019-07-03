package cn.swiftdev.example.framework.aop.aspect;

import java.lang.reflect.Method;

public interface LLJoinPoint {

    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key, Object value);

    Object getUserAttribute(String key);

}
