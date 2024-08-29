package top.sharehome.springbootinittemplate.model.vo.photo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * supanpan
 * @date 2024/08/27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AdminPhotoPageVo implements Serializable {
    /**
     * 照片ID
     */
    private Long photoId;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 照片存储URL
     */
    private String photoUrl;

    /**
     * 照片描述
     */
    private String photoDescription;

    /**
     * 上传时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除（0表示未删除，1表示已删除）
     */
    private Integer isDeleted;

    @Serial
    private static final long serialVersionUID = -267451380923346L;

}
