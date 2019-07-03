package cn.swiftdev.example.framework.annotation;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface LLAutowired {
    String value() default "";
}
