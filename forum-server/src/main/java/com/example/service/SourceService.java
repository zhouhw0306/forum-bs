package com.example.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.constant.Result;
import com.example.domain.dao.Source;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author zhw
* @description 针对表【tb_source】的数据库操作Service
* @createDate 2022-12-31 15:37:16
*/
public interface SourceService extends IService<Source> {

    Result<Integer> pass(Integer id, Integer type);

    IPage<Source> pageVo(String type, String sort, Integer pageNo, Integer pageSize);

    Result<Source> queryById(Integer id);
}
