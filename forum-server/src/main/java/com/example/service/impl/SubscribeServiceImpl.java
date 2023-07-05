package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domain.dao.Subscribe;
import com.example.service.SubscribeService;
import com.example.mapper.SubscribeMapper;
import org.springframework.stereotype.Service;

/**
* @author 24668
* @description 针对表【tb_subscribe(用户关注关系表)】的数据库操作Service实现
* @createDate 2022-10-04 15:54:50
*/
@Service
public class SubscribeServiceImpl extends ServiceImpl<SubscribeMapper, Subscribe>
    implements SubscribeService{

}




