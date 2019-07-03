package cn.swiftdev.example.framework.webmvc.servlet;

import java.util.Map;

public class LLModelAndView {

    private String viewName;

    private Map<String, ?> model;

    public LLModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public LLModelAndView(Map<String, ?> model) {
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }
}
