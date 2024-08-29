package top.sharehome.springbootinittemplate.controller.album;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.model.dto.photo.PhotoRequest;
import top.sharehome.springbootinittemplate.service.PhotoService;

import java.util.List;

/**
 * 照片接口 - 用户
 * supanpan
 * @date 2024/08/28
 */
@RestController
@RequestMapping("/photo")
public class AlbumController {

    @Resource
    private PhotoService photoService;

    /**
     * 在添加照片的同时关联标签
     * @param request
     * @return
     */
    @PostMapping("/addPhotoWithTags")
    public R<String> addPhotoWithTags(@RequestBody PhotoRequest request) {
        photoService.savePhotoWithTags(request.getPhoto(), request.getSelectedTagIds());
        return R.ok("添加成功");
    }
}
