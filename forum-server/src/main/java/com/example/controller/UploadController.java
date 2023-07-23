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
    public Result upload(MultipartFile image) {

        Result r = new Result();

        try {
            String filePath = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String baseFolder = "articleFile/" + filePath;

            QiNiuImage qiNiuImage = qiniuService.saveToQiNiu(image, baseFolder);

            r.setResultCode(ResultCode.SUCCESS);
            r.simple().put("url", qiNiuImage.getFileName());
        } catch (Exception e) {
            r.setResultCode(ResultCode.UPLOAD_ERROR);
        }

        return r;
    }
}
