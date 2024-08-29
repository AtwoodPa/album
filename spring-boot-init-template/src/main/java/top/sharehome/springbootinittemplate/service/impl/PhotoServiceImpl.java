package top.sharehome.springbootinittemplate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.mapper.PhotoMapper;
import top.sharehome.springbootinittemplate.mapper.PhotoTagMapper;
import top.sharehome.springbootinittemplate.mapper.TagMapper;
import top.sharehome.springbootinittemplate.mapper.UserMapper;
import top.sharehome.springbootinittemplate.model.dto.photo.AdminPhotoPageDto;
import top.sharehome.springbootinittemplate.model.dto.photo.PhotoRequest;
import top.sharehome.springbootinittemplate.model.entity.Photo;
import top.sharehome.springbootinittemplate.model.entity.PhotoTag;
import top.sharehome.springbootinittemplate.model.entity.Tag;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.model.page.PageModel;
import top.sharehome.springbootinittemplate.model.vo.photo.AdminPhotoPageVo;
import top.sharehome.springbootinittemplate.service.PhotoService;
import top.sharehome.springbootinittemplate.utils.oss.minio.MinioUtils;
import top.sharehome.springbootinittemplate.utils.satoken.LoginUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * panpan
 * @createDate 2024-08-27 14:58:25
 */
@Service
public class PhotoServiceImpl extends ServiceImpl<PhotoMapper, Photo> implements PhotoService {

    @Resource
    private PhotoMapper photoMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private TagMapper tagMapper;
    @Resource
    private PhotoTagMapper photoTagMapper;

    @Override
    public Page<AdminPhotoPageVo> adminPagePhoto(AdminPhotoPageDto adminPhotoPageDto, PageModel pageModel) {
        // 构建分页参数
        Page<Photo> page = pageModel.build();

        // 获取登录用户ID（如果不是管理员）
        Long loginUserId = LoginUtils.isAdmin() ? null : LoginUtils.getLoginUserId();

        // 执行分页查询并联表获取用户和标签信息
        List<AdminPhotoPageVo> records = photoMapper.selectPhotoWithUserAndTags(page, adminPhotoPageDto, loginUserId);

        // 将结果集与分页信息进行组合
        Page<AdminPhotoPageVo> res = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        res.setRecords(records);

        return res;
    }




    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminAddPhoto(Photo adminPhotoAddDto) {
        //        if(adminPhotoAddDto.getPhotoUrl() == null){
//            throw new CustomizeReturnException(ReturnCode.PHOTO_URL_IS_EMPTY);
//        }
        adminPhotoAddDto.setUserId(LoginUtils.getLoginUserId());
        // 对标签进行存储
        int insertResult = photoMapper.insert(adminPhotoAddDto);
        if (insertResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminDeletePhoto(Long id) {
        Photo photoInDatabase = photoMapper.selectById(id);
        // 无法对非存在或管理员账号进行操作
        if (Objects.isNull(photoInDatabase)) {
            throw new CustomizeReturnException(ReturnCode.PHOTO_DOES_NOT_EXIST);
        }
        // 删除照片信息
        int deleteResult = photoMapper.deleteById(id);
        if (deleteResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminUpdatePhoto(PhotoRequest adminPhotoUpdateDto) {
        Photo photo = adminPhotoUpdateDto.getPhoto();
        List<Long> tagIds = adminPhotoUpdateDto.getSelectedTagIds();
        Photo photoInDatabase = photoMapper.selectById(photo.getPhotoId());
        if (Objects.isNull(photoInDatabase)) {
            throw new CustomizeReturnException(ReturnCode.PHOTO_DOES_NOT_EXIST);
        }
        int updateResult = photoMapper.updateById(photo);
        // 维护照片和tag的关系
        int deleteTagResult = photoTagMapper.delete(Wrappers.<PhotoTag>lambdaQuery()
                .eq(PhotoTag::getPhotoId, photo.getPhotoId()));
        // 2. 维护照片与标签的关系
        if (deleteTagResult > 0) {
            for (Long tagId : tagIds) {
                PhotoTag photoTag = new PhotoTag();
                photoTag.setPhotoId(photo.getPhotoId());
                photoTag.setTagId(tagId);
                photoTagMapper.insert(photoTag);
            }
        }
        if (updateResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
    }

    /**
     * 上传照片 - 返回URL
     * @param file
     * @return
     */
    @Override
    public String uploadPhoto(MultipartFile file) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String photoPath = "photo/" + date;
        String url = MinioUtils.upload(file, photoPath);
        if (url == null) {
            throw new CustomizeReturnException(ReturnCode.FILE_UPLOAD_EXCEPTION);
        }
        return url;
    }

    /**
     * 保存照片与标签的关系
     * @param photo
     * @param tagIds
     */
    @Override
    public void savePhotoWithTags(Photo photo, List<Long> tagIds) {
        photo.setUserId(LoginUtils.getLoginUserId());
        // 1. 保存照片
        this.save(photo);

        // 2. 维护照片与标签的关系
        if (tagIds != null && !tagIds.isEmpty()) {
            for (Long tagId : tagIds) {
                PhotoTag photoTag = new PhotoTag();
                photoTag.setPhotoId(photo.getPhotoId());
                photoTag.setTagId(tagId);
                photoTagMapper.insert(photoTag);
            }
        }
    }
}
