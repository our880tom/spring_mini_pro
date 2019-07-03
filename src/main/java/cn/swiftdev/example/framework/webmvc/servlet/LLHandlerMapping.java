package cn.swiftdev.example.framework.webmvc.servlet;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class LLHandlerMapping {

    private Pattern pattern;

    private Object controller;

    private Method method;

    public LLHandlerMapping(Pattern pattern, Object controller, Method method) {
        this.pattern = pattern;
        this.controller = controller;
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Object getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }
}
