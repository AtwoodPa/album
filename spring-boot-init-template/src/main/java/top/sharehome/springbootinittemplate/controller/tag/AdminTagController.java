package top.sharehome.springbootinittemplate.controller.tag;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.config.log.annotation.ControllerLog;
import top.sharehome.springbootinittemplate.config.log.enums.Operator;
import top.sharehome.springbootinittemplate.model.entity.Tag;
import top.sharehome.springbootinittemplate.model.page.PageModel;
import top.sharehome.springbootinittemplate.model.vo.tag.AdminTagPageVo;
import top.sharehome.springbootinittemplate.service.TagService;

import java.util.List;

/**
 * 标签管理
 * supanpan
 * @date 2024/08/27
 */
@RestController
@RequestMapping("/admin/tag")
@SaCheckLogin
public class AdminTagController {
    @Resource
    private TagService tagService;
    @Resource
    private RedisTemplate<String, List<Tag>> redisTemplate;

    private static final String TAGS_CACHE_KEY = "all_tags";
    private static final long CACHE_EXPIRE_TIME = 10; // 缓存有效期：10分钟
    /**
     * 分页查询标签信息
     *
     * @param adminTagPageDto 标签信息查询条件
     * @param pageModel         分页模型
     * @return 分页查询结果
     */
    @GetMapping("/page")
    @ControllerLog(description = "管理员查询标签信息", operator = Operator.QUERY)
    public R<Page<AdminTagPageVo>> pageTag(Tag adminTagPageDto, PageModel pageModel) {
        Page<AdminTagPageVo> page = tagService.adminPageTag(adminTagPageDto, pageModel);
        return R.ok(page);
    }

    /**
     * 管理员添加标签
     *
     * @param adminTagAddDto 被添加标签信息
     * @return 添加结果
     */
    @PostMapping("/add")
    @ControllerLog(description = "管理员添加标签信息", operator = Operator.INSERT)
    public R<String> addTag(@RequestBody Tag adminTagAddDto) {
        tagService.adminAddTag(adminTagAddDto);
        // 清除缓存，确保下次获取的是最新数据
        redisTemplate.delete(TAGS_CACHE_KEY);
        return R.ok("添加成功");
    }

    /**
     * 管理员根据ID删除标签
     *
     * @param id 被删除标签的ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    @ControllerLog(description = "管理员删除标签信息", operator = Operator.DELETE)
    public R<String> deleteTag(@PathVariable("id") Long id) {
        tagService.adminDeleteTag(id);
        // 清除缓存，确保下次获取的是最新数据
        redisTemplate.delete(TAGS_CACHE_KEY);
        return R.ok("删除成功");
    }

    /**
     * 管理员修改标签信息
     *
     * @param adminTagUpdateDto 被修改后的标签信息
     * @return 修改结果
     */
    @PutMapping("/update")
    @ControllerLog(description = "管理员修改标签信息", operator = Operator.UPDATE)
    public R<String> updateTag(@RequestBody Tag adminTagUpdateDto) {
        tagService.adminUpdateTag(adminTagUpdateDto);
        // 清除缓存，确保下次获取的是最新数据
        redisTemplate.delete(TAGS_CACHE_KEY);
        return R.ok("修改成功");
    }
}
