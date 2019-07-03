package cn.swiftdev.example.framework.beans;

public class LLBeanWrapper {

    private Object wrappedInstance;

    //private Class<?> wrappedClass;


    public LLBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance() {
        return this.wrappedInstance;
    }

    public Class<?> getWrapperClass(){
        return this.wrappedInstance.getClass();
    }
}
