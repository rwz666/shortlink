package com.offer.shortlink.admin.toolkit;

import com.alibaba.excel.EasyExcel;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author rwz
 * @since 2025/2/16
 * EasyExcelWeb工具类
 */
public class EasyExcelWebUtil {

    @SneakyThrows
    public static void write(HttpServletResponse response, String fileName, Class<?> clazz, List<?> data) {
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename*="+ fileName +".xlsx");

        EasyExcel.write(response.getOutputStream(), clazz).sheet("Sheet").doWrite(data);
    }
}
