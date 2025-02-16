package com.offer.shortlink.project.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author rwz
 * @since 2025/1/26
 * 批量短链接创建返回对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkBatchCreateRespDTO {

    /**
     * 成功数量
     */
    private Integer total;

    /**
     * 批量创建短链接返回短链接信息
     */
    private List<ShortLinkBaseInfoRespDTO> baseLinkInfos;
}
