package top.sharehome.springbootinittemplate.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.model.dto.photo.AdminPhotoPageDto;
import top.sharehome.springbootinittemplate.model.dto.photo.PhotoRequest;
import top.sharehome.springbootinittemplate.model.entity.Photo;
import top.sharehome.springbootinittemplate.model.page.PageModel;
import top.sharehome.springbootinittemplate.model.vo.photo.AdminPhotoPageVo;

import java.util.List;

/**
* panpan
* @createDate 2024-08-27 14:58:25
*/
public interface PhotoService extends IService<Photo> {

    /**
     * 管理员分页查询照片信息
     * @param adminPhotoPageDto
     * @param pageModel
     * @return
     */
    Page<AdminPhotoPageVo> adminPagePhoto(AdminPhotoPageDto adminPhotoPageDto, PageModel pageModel);

    /**
     * 管理员添加照片
     * @param adminPhotoAddDto
     */
    void adminAddPhoto(Photo adminPhotoAddDto);

    /**
     * 管理员删除照片
     * @param id
     */
    void adminDeletePhoto(Long id);

    /**
     * 管理员更新照片
     * @param adminPhotoUpdateDto
     */
    void adminUpdatePhoto(PhotoRequest adminPhotoUpdateDto);

    String uploadPhoto(MultipartFile file);

    /**
     * 保存照片与标签的关系
     * @param photo
     * @param tagIds
     */
    void savePhotoWithTags(Photo photo, List<Long> tagIds);
}
