package top.sharehome.springbootinittemplate.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.sharehome.springbootinittemplate.model.entity.Tag;
import top.sharehome.springbootinittemplate.model.page.PageModel;
import top.sharehome.springbootinittemplate.model.vo.tag.AdminTagPageVo;

/**
* panpan
* @createDate 2024-08-27 14:58:26
*/
public interface TagService extends IService<Tag> {

    Page<AdminTagPageVo> adminPageTag(Tag adminTagPageDto, PageModel pageModel);

    void adminAddTag(Tag adminTagAddDto);

    void adminDeleteTag(Long id);

    void adminUpdateTag(Tag adminTagUpdateDto);

}
