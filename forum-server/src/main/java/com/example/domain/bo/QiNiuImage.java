package com.example.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author shw
 */
@Data
@AllArgsConstructor
public class QiNiuImage {

    /**
     * 文件所在bucket
     */
    private String bucket;

    /**
     * 文件的新名称，全局唯一，方便存入数据库
      */
    private String fileKey;

    /**
     * 外链地址，用于前端展示
     */
    private String path;

    /**
     * 文件在bucket下的存储目录,便于删除
      */
    private String fileName;

}
