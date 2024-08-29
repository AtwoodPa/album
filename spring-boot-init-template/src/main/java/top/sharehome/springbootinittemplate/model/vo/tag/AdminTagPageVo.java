package top.sharehome.springbootinittemplate.model.vo.tag;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * supanpan
 * @date 2024/08/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AdminTagPageVo implements Serializable {
    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * tag创建用户名
     */
    private String userName;

    /**
     * 标签名
     */
    private String tagName;

    /**
     * 创建时间
     */
    private Date createTime;

}
