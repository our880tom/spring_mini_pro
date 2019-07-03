import cn.swiftdev.example.framework.beans.LLBeanWrapper;

import java.lang.reflect.Method;

public class ReflectTest {
    public static void main(String[] args) {
        Class<?> clazz = LLBeanWrapper.class;
        Method[] methods = clazz.getMethods();

        System.out.println(methods[0].toString());
    }
}
