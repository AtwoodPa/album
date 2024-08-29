package top.sharehome.springbootinittemplate.model.dto.tag;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理员分页查询标签信息Dto类
 * panpan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AdminTagPageDto implements Serializable {

    /**
     * 标签名称
     */
    private String tagName;



    @TableField(exist = false)
    private static final long serialVersionUID = -22222222L ;


}