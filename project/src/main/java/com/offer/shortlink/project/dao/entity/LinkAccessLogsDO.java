package com.offer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.offer.shortlink.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author rwz
 * @since 2025/2/9
 * 短链接访问日志监控实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_link_access_logs")
public class LinkAccessLogsDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    private Long id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 用户信息
     */
    private String user;

    /**
     * 日期
     */
    private Date date;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * ip地址
     */
    private String ip;
}
