package cn.swiftdev.example.framework.webmvc.servlet;

import cn.swiftdev.example.framework.context.LLApplicationContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.swiftdev.example.framework.annotation.LLRequestMapping;
import cn.swiftdev.example.framework.annotation.LLController;

public class LLDispatcherServlet extends HttpServlet {

    private static final String LOCATION = "contextConfigLocation";

    private LLApplicationContext applicationContext;

    private List<LLHandlerMapping> handlerMappings = new ArrayList<>();

    private Map<LLHandlerMapping, LLHandlerAdapter> handlerAdapters = new HashMap<>();

    private List<LLViewResolver> viewResolvers = new ArrayList<>();

    public void init(ServletConfig config) throws ServletException {
        applicationContext = new LLApplicationContext(config.getInitParameter(LOCATION));
        initStrategies(applicationContext);

    }

    private void initStrategies(LLApplicationContext applicationContext) {

        //文件上传解析，如果请求类型是Multipart将通过
        initMultipartResolver(applicationContext);

        //本地化解析
        initLocalResolver(applicationContext);

        //主题解析
        initThemeResolver(applicationContext);

        //通过HandleMapping,将请求映射到处理器上
        initHandlerMappings(applicationContext);

        //通过HandlerAdapter进行多类型的参数动态匹配
        initHandlerAdapters(applicationContext);

        //如果执行过程中遇到异常，将交给HandlerExceptionResolver处理
        initHandlerExceptionResolvers(applicationContext);

        //直接解析逻辑视图到具体视图实现
        initRequestToViewNameTranslator(applicationContext);

        //通过viewResolver解析逻辑视图到具体视图实现
        initViewResolvers(applicationContext);
        //flash映射管理器
        initFlashMapManager(applicationContext);
    }

    private void initMultipartResolver(LLApplicationContext applicationContext) {
    }

    private void initLocalResolver(LLApplicationContext applicationContext) {
    }

    private void initThemeResolver(LLApplicationContext applicationContext) {
    }


    private void initHandlerMappings(LLApplicationContext applicationContext) {

        String[] beanNames = applicationContext.getBeanDefinitionNames();

        try{
            for (String beanName : beanNames) {
                Object object = applicationContext.getBean(beanName);
                Class<?> clazz = object.getClass();

                if (!clazz.isAnnotationPresent(LLController.class)){
                    continue;
                }

                String baseUrl = "";

                if (clazz.isAnnotationPresent(LLRequestMapping.class)){
                    baseUrl = clazz.getAnnotation(LLRequestMapping.class).value();
                }

                Method[] methods = clazz.getMethods();
                for (Method method : methods){
                    if(!method.isAnnotationPresent(LLRequestMapping.class)){
                        continue;
                    }

                    LLRequestMapping requestMapping = method.getAnnotation(LLRequestMapping.class);
                    String regex = ("/" + baseUrl + "/" + requestMapping.value().replaceAll("\\*", ".*")).replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);

                    handlerMappings.add(new LLHandlerMapping(pattern, object, method));

                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initHandlerAdapters(LLApplicationContext applicationContext) {
        for (LLHandlerMapping handlerMapping : handlerMappings){
            this.handlerAdapters.put(handlerMapping, new LLHandlerAdapter());
        }
    }

    private void initHandlerExceptionResolvers(LLApplicationContext applicationContext) {
    }

    private void initRequestToViewNameTranslator(LLApplicationContext applicationContext) {
    }

    private void initViewResolvers(LLApplicationContext applicationContext) {
        String templateRoot = applicationContext.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootFile = new File(templateRootPath);
        for (File file : templateRootFile.listFiles()){
            viewResolvers.add(new LLViewResolver(templateRootPath));
        }

    }
    private void initFlashMapManager(LLApplicationContext applicationContext) {
    }




    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        doPost(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        try {
            doDispatch(req, resp);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        //1.通过从request中那到url,去匹配一个handlerMapping
        LLHandlerMapping handlerMapping = getHandler(req);
        if (handlerMapping == null){
            processDispatchResult(req, resp, new LLModelAndView("404"));
            return;
        }
        //2.准备调用前的参数
        LLHandlerAdapter handlerAdapter = getHandlerAdapter(handlerMapping);
        //3.真正的调用，返回ModelAndView存储了要传给页面的值，和页面模版的名称
        LLModelAndView modelAndView = handlerAdapter.handler(req,resp,handlerMapping);
        //4.这一步是真正的输出
        processDispatchResult(req, resp, modelAndView);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, LLModelAndView llModelAndView) throws Exception {
        if (llModelAndView == null) return;
        if (viewResolvers.isEmpty()){
            return;
        }
        for (LLViewResolver viewResolver : viewResolvers){
            LLView view = viewResolver.resolverViewName(llModelAndView.getViewName(), null);
            view.render(llModelAndView.getModel(),req,resp);
        }
    }

    private LLHandlerAdapter getHandlerAdapter(LLHandlerMapping handlerMapping) {
        if (handlerAdapters.isEmpty()) {
            return null;
        }

        LLHandlerAdapter handlerAdapter = handlerAdapters.get(handlerMapping);
        if (handlerAdapter.supports(handlerMapping)){
            return handlerAdapter;
        }

        return null;
    }

    private LLHandlerMapping getHandler(HttpServletRequest req) throws Exception{
        if (handlerMappings.isEmpty()) {
            return null;
        }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");

        for (LLHandlerMapping handlerMapping: handlerMappings){
            Matcher matcher = handlerMapping.getPattern().matcher(url);
            if (matcher.matches()) {
                return handlerMapping;
            }

        }

        return null;
    }


}
