package cn.swiftdev.example.framework.webmvc.servlet;

import cn.swiftdev.example.framework.annotation.LLRequestMapping;
import cn.swiftdev.example.framework.annotation.LLRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LLHandlerAdapter {
    public boolean supports(Object handler){
        return handler instanceof LLHandlerMapping;
    }

    public LLModelAndView handler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{

        LLHandlerMapping handlerMapping = (LLHandlerMapping) handler;

        Map<String, Integer> paramIndexMapping = new HashMap<>();

        Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();

        for (int i = 0; i < pa.length; i ++){
            for (Annotation a : pa[i]){
                if (a instanceof LLRequestParam){
                    String paramName = ((LLRequestParam) a).value();
                    if (!"".equals(paramName)){
                        paramIndexMapping.put(paramName, i);
                    }

                }
            }
        }

        Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();

        for (int i = 0; i < paramTypes.length; i ++){
            Class<?> paramType = paramTypes[i];

            if (paramType == HttpServletRequest.class|| paramType == HttpServletResponse.class){
                paramIndexMapping.put(paramType.getName(), i);
            }
        }

        Map<String, String[]> params = request.getParameterMap();
        Object[] paramValues = new Object[params.size()];

        for (Map.Entry<String, String[]> paramEntry : params.entrySet()){
            String value = Arrays.toString(paramEntry.getValue()).replaceAll("\\[|\\]","")
                    .replaceAll("\\s",",");

            if (!paramIndexMapping.containsKey(paramEntry.getKey())){
                continue;
            }

            int index = paramIndexMapping.get(paramEntry.getKey());
            paramValues[index] = caseStringValue(value, paramTypes[index]);
        }

        if (paramIndexMapping.containsKey(HttpServletRequest.class.getName())){
            int requestIndex = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[requestIndex] = request;
        }

        if (paramIndexMapping.containsKey(HttpServletResponse.class.getName())){
            int responseIndex = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[responseIndex] = response;
        }

        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);
        if (result == null || result.getClass() == Void.class){
            return null;
        }

        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == LLModelAndView.class;
        if (isModelAndView) {
            return (LLModelAndView)result;
        }

        return null;
    }

    private Object caseStringValue(String value, Class<?> paramType){
        if (String.class == paramType){
            return value;
        }

        if (Integer.class == paramType){
            return Integer.valueOf(value);
        }

        if (Double.class == paramType){
            return Double.valueOf(value);
        }
        return null;
    }
}
