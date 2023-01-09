package com.example.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.constant.Result;
import com.example.domain.Source;
import com.example.domain.SourceHasfavour;
import com.example.domain.SourceHasthumb;
import com.example.domain.User;
import com.example.service.SourceHasfavourService;
import com.example.service.SourceHasthumbService;
import com.example.service.SourceService;
import com.example.service.UserService;
import com.example.utils.UserUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author 24668
 */
@RestController
@RequestMapping("source")
public class SourceController {

    @Resource
    private SourceService sourceService;

    @Resource
    private UserService userService;

    @Resource
    private SourceHasfavourService sourceHasfavourService;

    @Resource
    private SourceHasthumbService sourceHasthumbService;

    @PostMapping("vo")
    public IPage pageVo(String type,Integer pageNo,Integer pageSize){
        QueryWrapper<Source> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category",type);
        IPage<Source> page = new Page<>(pageNo,pageSize);
        IPage<Source> iPage = sourceService.page(page, queryWrapper);
        List<Source> records = iPage.getRecords();

        String currentUser = UserUtils.getCurrentUser();

        for (Source record : records) {
            User byId = userService.getById(record.getUserId());
            record.setUser(byId);
            // 封装点赞 收藏
            if (currentUser != null) {
                QueryWrapper qw = new QueryWrapper<>();
                qw.eq("user_id",currentUser);
                qw.eq("source_id",record.getId());
                SourceHasthumb thumb = sourceHasthumbService.getOne(qw);
                SourceHasfavour favour = sourceHasfavourService.getOne(qw);
                record.setHasThumb(thumb != null);
                record.setHasFavour(favour != null);
            }
        }
        return iPage;
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id){
        Source record = sourceService.getById(id);
        User byId = userService.getById(record.getUserId());
        record.setUser(byId);
        String currentUser = UserUtils.getCurrentUser();
        // 封装点赞 收藏
        if (currentUser != null) {
            QueryWrapper qw = new QueryWrapper<>();
            qw.eq("user_id",currentUser);
            qw.eq("source_id",record.getId());
            SourceHasthumb thumb = sourceHasthumbService.getOne(qw);
            SourceHasfavour favour = sourceHasfavourService.getOne(qw);
            record.setHasThumb(thumb != null);
            record.setHasFavour(favour != null);
        }
        return Result.success(record);
    }
}
