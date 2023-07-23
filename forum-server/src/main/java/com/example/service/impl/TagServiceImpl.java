package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domain.dao.Tag;
import com.example.service.TagService;
import com.example.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author zhw
* @description 针对表【tb_tag】的数据库操作Service实现
* @createDate 2022-09-27 15:26:43
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




