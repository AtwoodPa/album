package top.sharehome.springbootinittemplate.config.captcha.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 校验验证码参数实体类
 *
 * 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Captcha implements Serializable {

    private static final long serialVersionUID = -6070139749121232739L;

    /**
     * 验证码
     */
    private String code;

    /**
     * 验证码UUID
     */
    private String uuid;

}