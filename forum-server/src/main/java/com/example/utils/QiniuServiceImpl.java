package com.example.utils;

import com.example.domain.bo.QiNiuImage;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.UUID;

/**
 * @author zhw
 * 七牛云上传文件工具类
 */
@Slf4j
@Component
public class QiniuServiceImpl {

    @Value(value = "${qiniu.AccessKey}")
    private String accessKey;
    @Value(value = "${qiniu.SecretKey}")
    private String secretKey;
    @Value("${qiniu.bucket}")
    private String bucket;
    @Value("${qiniu.url}")
    private String url;

    /** 七牛文件上传管理器 */
    private UploadManager uploadManager;

    /** 七牛文件管理器 */
    private BucketManager bucketManager;

    /** 七牛云认证工具 */
    private Auth auth;

    /** 七牛云上传的token */
    private String token;

    @PostConstruct
    private void init() {
        uploadManager = new UploadManager(new Configuration(Region.huanan()));
        auth = Auth.create(accessKey, secretKey);
        bucketManager = new BucketManager(auth, new Configuration(Region.huanan()));
        token = auth.uploadToken(bucket);
    }

    /**
     * 上传文件至七牛云
     * @param multipartFile 文件
     * @return 文件外链地址
     */
    public QiNiuImage saveToQiNiu(MultipartFile multipartFile,String prePath){
        try {
            // 1.获取文件上传的流
            byte[] fileBytes = multipartFile.getBytes();
            // 2.获取文件名
            String originalFilename = multipartFile.getOriginalFilename();
            assert originalFilename != null;
            // 3.文件类型
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 4.生成新的文件名
            String fileKey = UUID.randomUUID().toString().replace("-", "");
            String bucket = this.bucket;
            // 5.文件在bucket下的存储目录
            String path = prePath+"/"+ fileKey + suffix;

            // 6.上传图片至七牛云
            uploadManager.put(fileBytes,path,token);

            // 这里进行了自定义封装，
            /**
             * {bucket: "文件所在bucket“,
             * fileKey: "文件的新名称，全局唯一，方便存入数据库”,
             * path: "外链地址，用于前端展示"
             * fileName: "文件在bucket下的存储目录,便于删除"}
             */
            return new QiNiuImage(bucket, fileKey,  path,url+"/"+path);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param bucketName 空间名称
     * @param fileName 文件存储位置：如 /2023/1/9/sadihfuehjdshdcjbhuasd.png
     * @return 是否删除
     */
    public boolean removeImageQiniu(String bucketName, String fileName) {
        try {
            bucketManager.delete(bucketName, fileName);
        } catch (QiniuException e) {
            e.printStackTrace();
        }
        return true;
    }

}

