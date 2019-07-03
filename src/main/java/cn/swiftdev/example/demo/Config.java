package cn.swiftdev.example.demo;

import lombok.Data;

@Data
public class Config {
    private String className;

    private String beanName;

    public static void main(String[] args) {
        new Config().getBeanName();
    }
}
