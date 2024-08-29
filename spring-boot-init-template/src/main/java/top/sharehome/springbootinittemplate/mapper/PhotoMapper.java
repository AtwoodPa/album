package top.sharehome.springbootinittemplate.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.sharehome.springbootinittemplate.model.dto.photo.AdminPhotoPageDto;
import top.sharehome.springbootinittemplate.model.entity.Photo;
import top.sharehome.springbootinittemplate.model.vo.photo.AdminPhotoPageVo;

import java.util.List;

/**
* panpan
* @createDate 2024-08-27 14:58:25
*/
@Mapper
public interface PhotoMapper extends BaseMapper<Photo> {


    List<AdminPhotoPageVo> selectPhotoWithUserAndTags(@Param("page") Page<?> page,
                                                      @Param("adminPhotoPageDto") AdminPhotoPageDto adminPhotoPageDto,
                                                      @Param("loginUserId") Long loginUserId);
}
