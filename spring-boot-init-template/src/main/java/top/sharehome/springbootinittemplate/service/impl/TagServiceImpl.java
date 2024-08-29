package top.sharehome.springbootinittemplate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.sharehome.springbootinittemplate.mapper.PhotoTagMapper;
import top.sharehome.springbootinittemplate.mapper.TagMapper;
import top.sharehome.springbootinittemplate.mapper.UserMapper;
import top.sharehome.springbootinittemplate.model.entity.PhotoTag;
import top.sharehome.springbootinittemplate.model.entity.Tag;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.model.page.PageModel;
import top.sharehome.springbootinittemplate.model.vo.tag.AdminTagPageVo;
import top.sharehome.springbootinittemplate.service.TagService;
import top.sharehome.springbootinittemplate.utils.satoken.LoginUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
* panpan
* @createDate 2024-08-27 14:58:26
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Resource
    private TagMapper tagMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private PhotoTagMapper photoTagMapper;
    @Override
    public Page<AdminTagPageVo> adminPageTag(Tag adminTagPageDto, PageModel pageModel) {
        // 构建分页参数
        Page<Tag> page = pageModel.build();
        Page<AdminTagPageVo> res = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        // 构建查询参数
        LambdaQueryWrapper<Tag> qw = new LambdaQueryWrapper<>();

        // 判断当前用户是否为管理员
        if (!LoginUtils.isAdmin()) {
            // 如果不是管理员，只显示当前用户添加的标签
            qw.eq(Tag::getUserId, LoginUtils.getLoginUserId());
        }

        // 如果描述不为空，添加模糊查询条件
        if (StringUtils.isNotBlank(adminTagPageDto.getTagName())) {
            qw.like(Tag::getTagName, adminTagPageDto.getTagName());
        }

        // 排序
        qw.orderByDesc(Tag::getCreateTime);

        // 分页查询
        tagMapper.selectPage(page, qw);

        // 返回值处理（Entity ==> Vo）
        List<AdminTagPageVo> newRecords = page.getRecords().stream().map(item -> {
            // 查询用户信息
            User user = userMapper.selectById(item.getUserId());
            String userName = user != null ? user.getName() : "未知用户";
            // 创建并返回 AdminPhotoPageVo 对象
            AdminTagPageVo tagPageVo = new AdminTagPageVo()
                    .setTagId(item.getTagId())
                    .setTagName(item.getTagName())
                    .setUserId(item.getUserId())
                    .setUserName(userName)
                    .setCreateTime(item.getCreateTime());
            return tagPageVo;
        }).collect(Collectors.toList());

        // 将处理后的记录集复制到分页对象中
        BeanUtils.copyProperties(page, res, "records");
        res.setRecords(newRecords);
        return res;
    }

    @Override
    public void adminAddTag(Tag adminTagAddDto) {
        adminTagAddDto.setUserId(LoginUtils.getLoginUserId());
        tagMapper.insert(adminTagAddDto);
    }

    @Override
    public void adminDeleteTag(Long id) {
        tagMapper.deleteById(id);
        // 删除与该标签相关的照片关联
        LambdaQueryWrapper<PhotoTag> eq = new LambdaQueryWrapper<PhotoTag>().eq(PhotoTag::getTagId, id);
        photoTagMapper.delete(eq);
    }

    @Override
    public void adminUpdateTag(Tag adminTagUpdateDto) {
        tagMapper.updateById(adminTagUpdateDto);
    }
}
