package top.sharehome.springbootinittemplate.config.ip2region.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import top.sharehome.springbootinittemplate.config.ip2region.properties.enums.LoadType;

/**
 * 离线IP库配置属性
 *
 * @author
 */
@Data
@ConfigurationProperties(prefix = "ip2region")
public class Ip2RegionProperties {

    /**
     * 加载数据方式，默认为内存加载
     */
    private LoadType loadType = LoadType.MEMORY;

}
