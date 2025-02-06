package com.offer.shortlink.admin.remote.dto.req;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/**
 * @author rwz
 * @since 2025/1/27
 * 短链接分页查询请求对象
 */
@Data
public class ShortLinkRecycleBinPageReqDTO extends Page {

    /**
     * 分组标识集合
     */
    private List<String> gidList;

}
