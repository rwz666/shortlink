package com.offer.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.offer.shortlink.admin.dao.entity.GroupDO;

/**
 * @author rwz
 * @since 2025/1/24
 * 短链接分组接口层
 */
public interface GroupService extends IService<GroupDO> {

    /**
     * 新增分组
     *
     * @param groupName 分组名称
     */
    void saveGroup(String groupName);
}
