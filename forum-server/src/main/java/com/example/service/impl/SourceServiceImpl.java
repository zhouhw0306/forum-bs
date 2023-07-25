package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.constant.Result;
import com.example.constant.SourceEnum;
import com.example.domain.dao.Source;
import com.example.domain.dao.SourceHasfavour;
import com.example.domain.dao.SourceHasthumb;
import com.example.domain.dao.User;
import com.example.mapper.UserMapper;
import com.example.service.SourceHasfavourService;
import com.example.service.SourceHasthumbService;
import com.example.service.SourceService;
import com.example.mapper.SourceMapper;
import com.example.service.UserService;
import com.example.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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

    @Resource
    private SourceHasfavourService sourceHasfavourService;

    @Resource
    private SourceHasthumbService sourceHasthumbService;

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

    @Override
    public IPage<Source> pageVo(String type, String sort, Integer pageNo, Integer pageSize) {
        QueryWrapper<Source> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category", SourceEnum.findEnumByName(type));
        queryWrapper.eq("state", 1);
        queryWrapper.orderByDesc(sort);
        IPage<Source> page = new Page<>(pageNo,pageSize);
        IPage<Source> iPage = page(page, queryWrapper);
        List<Source> records = iPage.getRecords();

        for (Source record : records) {
            User byId = userMapper.selectById(record.getUserId());
            record.setUser(byId);
            // 封装点赞 收藏
            String currentUser = UserUtils.getCurrentUser();
            if (currentUser != null) {
                hasFavourOrThumb(record,currentUser);
            }
        }
        return iPage;
    }

    @Override
    public Result queryById(Integer id) {
        Source record = getById(id);
        record.setUser(userMapper.selectById(record.getUserId()));
        // 封装点赞 收藏
        String currentUser = UserUtils.getCurrentUser();
        if (currentUser != null) {
            hasFavourOrThumb(record,currentUser);
        }
        return Result.success(record);

    }

    /**
     * 判断用户是否点赞或收藏该资源
     */
    public void hasFavourOrThumb(Source source,String currentUser) {
        SourceHasthumb thumb = sourceHasthumbService.lambdaQuery()
                .eq(SourceHasthumb::getUserId,currentUser)
                .eq(SourceHasthumb::getSourceId,source.getId())
                .one();
        SourceHasfavour favour = sourceHasfavourService.lambdaQuery()
                .eq(SourceHasfavour::getUserId,currentUser)
                .eq(SourceHasfavour::getSourceId,source.getId())
                .one();
        source.setHasThumb(thumb != null);
        source.setHasFavour(favour != null);
    }
}




