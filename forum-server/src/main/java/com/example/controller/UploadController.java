package com.example.controller;

import com.example.constant.Result;
import com.example.constant.ResultCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 上传接口
 * @author 24668
 */
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
                .append(request.getContextPath())
                .append("/articleFile/")
                .append(filePath);

        String imgName = UUID.randomUUID() + "_" + image.getOriginalFilename().replaceAll(" ", "");

        try {

            File dest = new File(baseFolder, imgName);
            image.transferTo(dest);

            url.append("/").append(imgName);

            r.setResultCode(ResultCode.SUCCESS);

            r.simple().put("url", url);

        } catch (IOException e) {
            r.setResultCode(ResultCode.UPLOAD_ERROR);
        }

        return r;
    }
}
