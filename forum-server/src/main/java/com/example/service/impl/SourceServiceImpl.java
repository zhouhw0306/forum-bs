package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domain.Source;
import com.example.service.SourceService;
import com.example.mapper.SourceMapper;
import org.springframework.stereotype.Service;

/**
* @author 24668
* @description 针对表【tb_source】的数据库操作Service实现
* @createDate 2022-12-31 15:37:16
*/
@Service
public class SourceServiceImpl extends ServiceImpl<SourceMapper, Source>
    implements SourceService{

}




