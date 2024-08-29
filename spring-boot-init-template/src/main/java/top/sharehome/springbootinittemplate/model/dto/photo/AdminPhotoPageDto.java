package top.sharehome.springbootinittemplate.model.dto.photo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理员分页查询照片信息Dto类
 * panpan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AdminPhotoPageDto implements Serializable {

    /**
     * 照片描述
     */
    private String photoDescription;

    /**
     * 上传用户
     */
    private String userAccount;

    /**
     * 上传时间
     */
    private Date uploadTime;


    @TableField(exist = false)
    private static final long serialVersionUID = -22222222L ;


}