package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.constant.Result;
import com.example.domain.dao.Source;
import com.example.mapper.UserMapper;
import com.example.service.SourceService;
import com.example.mapper.SourceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
* @author zhw
* @description 针对表【tb_source】的数据库操作Service实现
* @createDate 2022-12-31 15:37:16
*/
@Service
public class SourceServiceImpl extends ServiceImpl<SourceMapper, Source>
    implements SourceService{

    @Resource
    UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result pass(Integer id, Integer type) {
        if (type == null){
            return Result.error();
        }
        // 拒绝
        if (type == 0){
            removeById(id);
            return Result.success(0);
        }
        // 通过
        if (type == 1){
            Source source = new Source();
            source.setId(id);
            source.setState(1);
            updateById(source);
            Source sou = getById(id);
            userMapper.addScore(sou.getUserId(),2);
            return Result.success(1);
        }
        return Result.error();
    }
}




