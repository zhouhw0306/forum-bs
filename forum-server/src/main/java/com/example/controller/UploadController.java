package com.example.controller;

import cn.hutool.core.lang.Assert;
import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.utils.ImgAddWatermarkUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 上传接口
 * @author 24668
 */
@Slf4j
@RestController
public class UploadController {

    @Value("${me.upload.path}")
    private String baseFolderPath;

    @PostMapping("/upload")
    public Result upload(HttpServletRequest request, MultipartFile image) {

        Result r = new Result();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        StringBuffer url = new StringBuffer();
        String filePath = sdf.format(new Date());

        File baseFolder = new File(baseFolderPath + filePath);
        if (!baseFolder.exists()) {
            baseFolder.mkdirs();
        }

        url.append(request.getScheme())
                .append("://")
                .append(request.getServerName())
                .append(":")
                .append(request.getServerPort())
                .append("/articleFile/")
                .append(filePath);

        String imgName = UUID.randomUUID() + "_" + image.getOriginalFilename().replaceAll(" ", "");

        try {

            //File dest = new File(baseFolder, imgName);
            //image.transferTo(dest);
            File tempFile = ImgAddWatermarkUtil.multipartFileToFile(image);
            ImgAddWatermarkUtil.addWatermark(tempFile,baseFolderPath+filePath+"/"+imgName);
            Assert.isTrue(tempFile.delete());
            url.append("/").append(imgName);

            r.setResultCode(ResultCode.SUCCESS);

            r.simple().put("url", url);

        } catch (Exception e) {
            r.setResultCode(ResultCode.UPLOAD_ERROR);
        }

        return r;
    }
}
