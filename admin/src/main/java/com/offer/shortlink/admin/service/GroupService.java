package com.offer.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.offer.shortlink.admin.dao.entity.GroupDO;
import com.offer.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.offer.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;

import java.util.List;

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

    /**
     * 查询用户短链接分组集合
     */
    List<ShortLinkGroupRespDTO> listGroup();

    /**
     * 删除分组
     *
     * @param requestParam 短链接分组更新参数
     */
    void updateGroup(ShortLinkGroupUpdateReqDTO requestParam);
}
