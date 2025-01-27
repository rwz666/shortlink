package com.offer.shortlink.project.dto.req;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.offer.shortlink.project.dao.entity.ShortLinkDO;
import lombok.Data;

/**
 * @author rwz
 * @since 2025/1/27
 * 短链接分页查询请求对象
 */
@Data
public class ShortLinkPageReqDTO extends Page<ShortLinkDO> {

    /**
     * 分组标识
     */
    private String gid;

}
