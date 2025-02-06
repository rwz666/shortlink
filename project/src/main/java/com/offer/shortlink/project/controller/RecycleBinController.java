package com.offer.shortlink.project.controller;

import com.offer.shortlink.project.common.convention.result.Result;
import com.offer.shortlink.project.common.convention.result.Results;
import com.offer.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.offer.shortlink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rwz
 * @since 2025/2/6
 * 回收站管理控制层
 */
@RestController
@RequiredArgsConstructor
public class RecycleBinController {

    private final RecycleBinService recycleBinService;

    /**
     * 新增回收站数据
     */
    @PostMapping("/api/short-link/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam){
        recycleBinService.saveRecycleBin(requestParam);
        return Results.success();
    }
}
