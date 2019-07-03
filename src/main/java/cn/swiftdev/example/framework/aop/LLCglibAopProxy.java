package cn.swiftdev.example.framework.aop;

import cn.swiftdev.example.framework.aop.support.LLAdvisedSupport;

public class LLCglibAopProxy implements LLAopProxy {

    private LLAdvisedSupport advisedSupport;

    public LLCglibAopProxy(LLAdvisedSupport advisedSupport) {
        this.advisedSupport = advisedSupport;
    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override

    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
