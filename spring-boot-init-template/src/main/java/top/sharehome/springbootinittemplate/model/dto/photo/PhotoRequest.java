package top.sharehome.springbootinittemplate.model.dto.photo;

import lombok.Data;
import top.sharehome.springbootinittemplate.model.entity.Photo;

import java.util.List;

/**
 * 添加照片请求体
 *
 * panpan
 */
@Data
public class PhotoRequest {
    private Photo photo;
    private List<Long> selectedTagIds;

    // Getters and Setters
}