package cn.swiftdev.example.framework.context.support;

import cn.swiftdev.example.framework.context.LLApplicationContext;

public interface LLApplicationContextAware {
    void setApplicationContext(LLApplicationContext applicationContext);
}
