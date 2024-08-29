package top.sharehome.springbootinittemplate.controller.album;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.log.annotation.ControllerLog;
import top.sharehome.springbootinittemplate.config.log.enums.Operator;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.model.dto.photo.AdminPhotoPageDto;
import top.sharehome.springbootinittemplate.model.dto.photo.PhotoRequest;
import top.sharehome.springbootinittemplate.model.entity.Photo;
import top.sharehome.springbootinittemplate.model.page.PageModel;
import top.sharehome.springbootinittemplate.model.vo.photo.AdminPhotoPageVo;
import top.sharehome.springbootinittemplate.service.PhotoService;

import java.util.ArrayList;
import java.util.List;

/**
 * 照片接口 - 管理员
 * supanpan
 * @date 2024/08/27
 */
@RestController
@RequestMapping("/admin/photo")
@SaCheckLogin
//@SaCheckRole(value = {Constants.ROLE_ADMIN})
public class AdminAlbumController {
    @Resource
    private PhotoService photoService;

    /**
     * 照片最大大小
     */
    private static final int PHOTO_MAX_SIZE = 1024 * 1024 * 10;

    /**
     * 照片文件后缀集合
     */
    private static final List<String> PHOTO_SUFFIX_LIST = new ArrayList<String>() {
        {
            add("png");
            add("jpg");
            add("jpeg");
        }
    };


    @SaIgnore
    @CrossOrigin(value = {"http://43.142.255.148"}, allowedHeaders = {"*"})
    @PostMapping(value = "/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<String> uploadPhoto(@RequestPart("file") MultipartFile file) {
        // 校验文件大小是否符合要求
        if (file.getSize() == 0 || file.getSize() > PHOTO_MAX_SIZE) {
            throw new CustomizeReturnException(ReturnCode.USER_UPLOADED_FILE_IS_TOO_LARGE, "上传照片大小不得大于10MB");
        }

        // 校验文件后缀名是否符合要求
        String originalName = StringUtils.isNotBlank(file.getOriginalFilename()) ? file.getOriginalFilename() : file.getName();
        String suffix = FilenameUtils.getExtension(originalName).toLowerCase();
        if (!PHOTO_SUFFIX_LIST.contains(suffix)) {
            throw new CustomizeReturnException(ReturnCode.USER_UPLOADED_FILE_TYPE_MISMATCH, "头像仅支持png和jpg、jpeg格式");
        }

        // 上传照片，并获取URL
        String url = photoService.uploadPhoto(file);

        // 检查上传是否成功
        if (url == null) {
            throw new CustomizeReturnException(ReturnCode.FILE_UPLOAD_EXCEPTION, "照片上传失败");
        }

        // 返回照片的URL
        return R.ok(url);
    }

    /**
     * 分页查询照片信息
     *
     * @param adminPhotoPageDto 照片信息查询条件
     * @param pageModel         分页模型
     * @return 分页查询结果
     */
    @GetMapping("/page")
    @ControllerLog(description = "管理员查询照片信息", operator = Operator.QUERY)
    public R<Page<AdminPhotoPageVo>> pagePhoto(AdminPhotoPageDto adminPhotoPageDto, PageModel pageModel) {
        Page<AdminPhotoPageVo> page = photoService.adminPagePhoto(adminPhotoPageDto, pageModel);
        return R.ok(page);
    }

    /**
     * 管理员添加照片
     *
     * @param adminPhotoAddDto 被添加照片信息
     * @return 添加结果
     */
    @PostMapping("/add")
    @ControllerLog(description = "管理员添加照片信息", operator = Operator.INSERT)
    public R<String> addPhoto(@RequestBody Photo adminPhotoAddDto) {
        photoService.adminAddPhoto(adminPhotoAddDto);
        return R.ok("添加成功");
    }


    /**
     * 管理员根据ID删除照片
     *
     * @param id 被删除照片的ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    @ControllerLog(description = "管理员删除照片信息", operator = Operator.DELETE)
    public R<String> deletePhoto(@PathVariable("id") Long id) {
        photoService.adminDeletePhoto(id);
        return R.ok("删除成功");
    }

    /**
     * 管理员修改照片信息
     *
     * @return 修改结果
     */
    @PutMapping("/update")
    @ControllerLog(description = "管理员修改照片信息", operator = Operator.UPDATE)
    public R<String> updatePhoto(@RequestBody PhotoRequest request) {
        photoService.adminUpdatePhoto(request);
        return R.ok("修改成功");
    }
}
