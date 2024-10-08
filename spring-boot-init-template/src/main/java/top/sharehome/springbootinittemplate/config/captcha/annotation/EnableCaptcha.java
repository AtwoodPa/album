package top.sharehome.springbootinittemplate.config.captcha.annotation;

import java.lang.annotation.*;

/**
 * 需要验证码的方法注解
 *
 * 
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableCaptcha {

}