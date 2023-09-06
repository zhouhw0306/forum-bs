package com.example.controller;

import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.domain.bo.QiNiuImage;
import com.example.utils.QiniuServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhw
 */
@Slf4j
@RestController
@Api(tags = "上传文章图片接口")
public class UploadController {

    @Resource
    QiniuServiceImpl qiniuService;

    @PostMapping("/upload")
    @ApiOperation(value = "上传文章中的图片")
    public Result<Map<String, Object>> upload(MultipartFile image) {
        try {
            String filePath = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String baseFolder = "articleFile/" + filePath;

            QiNiuImage qiNiuImage = qiniuService.saveToQiNiu(image, baseFolder);

            Map<String, Object> map = new HashMap<>(1);
            map.put("url", qiNiuImage.getFileName());
            return Result.simple(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error(ResultCode.UPLOAD_ERROR);
    }
}
