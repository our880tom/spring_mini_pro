package cn.swiftdev.example.framework.aop.config;

import lombok.Data;

@Data
public class LLAopConfig {

    private String pointCut;

    private String aspectBefore;

    private String aspectAfter;

    private String aspectClass;

    private String aspectAfterThrow;

    private String aspectAfterThrowingName;
}
