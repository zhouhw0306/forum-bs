package com.example.mapper;

import com.example.domain.dao.Tag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Collection;

/**
* @author zhw
* @description 针对表【tb_tag】的数据库操作Mapper
* @createDate 2022-09-27 15:26:42
* @Entity com.example.domain.dao.Tag
*/
public interface TagMapper extends BaseMapper<Tag> {

    Integer insertBatchSomeColumn(Collection<Tag> entityList);
}




