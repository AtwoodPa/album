package top.sharehome.springbootinittemplate.controller.tag;

import cn.dev33.satoken.annotation.SaIgnore;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.model.entity.Tag;
import top.sharehome.springbootinittemplate.service.TagService;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * panpan
 * @date 2024/08/28
 */
@RestController
@RequestMapping("/tag")
public class TagController {

    @Resource
    private TagService tagService;

    @Resource
    private RedisTemplate<String, List<Tag>> redisTemplate;

    private static final String TAGS_CACHE_KEY = "all_tags";
    private static final long CACHE_EXPIRE_TIME = 10; // 缓存有效期：10分钟

    /**
     * 获取所有标签
     * @return
     */
    @SaIgnore
    @GetMapping("/allTags")
    public R<List<Tag>> getAllTags() {
        // 尝试从Redis中获取缓存的数据
        List<Tag> tags = redisTemplate.opsForValue().get(TAGS_CACHE_KEY);

        if (tags != null) {
            // 如果缓存命中，直接返回
            return R.ok("获取所有标签成功（缓存）", tags);
        }

        // 如果缓存未命中，从数据库中查询
        tags = tagService.list();

        // 将查询到的数据放入Redis缓存，并设置过期时间
        redisTemplate.opsForValue().set(TAGS_CACHE_KEY, tags, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);

        return R.ok("获取所有标签成功", tags);
    }
}
