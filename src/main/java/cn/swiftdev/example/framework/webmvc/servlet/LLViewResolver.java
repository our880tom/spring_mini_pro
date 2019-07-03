package cn.swiftdev.example.framework.webmvc.servlet;

import java.io.File;
import java.util.Locale;

public class LLViewResolver {
    private final String DEFAULT_TEMPELATE_SUFFIX = ".html";

    private File templateRootDir;

    public LLViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        templateRootDir = new File(templateRootPath);
    }

    public LLView resolverViewName(String viewName, Locale locale){
        if (viewName == null ||"".equals(viewName.trim())){
            return null;
        }

        viewName = viewName.endsWith(DEFAULT_TEMPELATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPELATE_SUFFIX);
        File tamplateFile = new File((this.templateRootDir + "/" + viewName).replaceAll("/+",""));

        return new LLView(tamplateFile);
    }
}



