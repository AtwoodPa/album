package top.sharehome.springbootinittemplate.config.log.enums;

import lombok.Getter;

/**
 * 日志请求方法枚举类
 *
 * 
 */
@Getter
public enum RequestMethod {

    GET("GET"),

    POST("POST"),

    PUT("PUT"),

    DELETE("DELETE"),

    PATCH("PATCH");

    private final String methodName;

    RequestMethod(String methodName) {
        this.methodName = methodName;
    }

}
