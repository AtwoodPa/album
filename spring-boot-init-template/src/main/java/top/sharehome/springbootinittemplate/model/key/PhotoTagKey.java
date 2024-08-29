package top.sharehome.springbootinittemplate.model.key;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 复合主键实体类
 * panpan
 */
@Data
public class PhotoTagKey implements Serializable {
    @TableField("photo_id")
    private Long photoId;
    @TableField("tag_id")
    private Long tagId;
}