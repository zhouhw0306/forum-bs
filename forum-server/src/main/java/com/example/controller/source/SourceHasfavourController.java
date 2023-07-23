package com.example.controller.source;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.domain.dao.Source;
import com.example.domain.dao.SourceHasfavour;
import com.example.domain.dao.User;
import com.example.service.SourceHasfavourService;
import com.example.service.SourceService;
import com.example.service.UserService;
import com.example.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("source")
@Api(tags = "资源收藏操作接口")
public class SourceHasfavourController {

    @Resource
    private SourceHasfavourService sourceHasfavourService;

    @Resource
    private UserService userService;

    @Resource
    private SourceService sourceService;

    @PostMapping("favour")
    @ApiOperation(value = "收藏或取消收藏指定资源")
    public Result favour(Integer targetId){
        QueryWrapper<SourceHasfavour> queryWrapper = new QueryWrapper<>();
        String currentUser = UserUtils.getCurrentUser();
        if (currentUser == null ){
            return Result.error(ResultCode.USER_NOT_LOGGED_IN);
        }
        User user = userService.getById(currentUser);
        if (user == null){
            return Result.error(ResultCode.USER_NOT_LOGGED_IN);
        }
        queryWrapper.eq("user_id",currentUser);
        queryWrapper.eq("source_id",targetId);
        SourceHasfavour one = sourceHasfavourService.getOne(queryWrapper);
        Source source = sourceService.getById(targetId);
        if (one == null){
            //添加收藏关系
            sourceHasfavourService.save(SourceHasfavour.builder().userId(currentUser).sourceId(targetId).build());
            source.setFavourNum(source.getFavourNum()+1);
            sourceService.updateById(source);
            return Result.success(1);
        }else {
            //删除收藏关系
            sourceHasfavourService.remove(queryWrapper);
            source.setFavourNum(source.getFavourNum()-1);
            sourceService.updateById(source);
            return Result.success(-1);
        }
    }

    @GetMapping("getHasFavour")
    @ApiOperation(value = "获得所有收藏")
    public Result getHasFavour(){
        String userId = UserUtils.getCurrentUser();
        if (userId == null){
            return Result.error(ResultCode.USER_NOT_LOGGED_IN);
        }
        List<SourceHasfavour> favourList = sourceHasfavourService.query().eq("user_id", userId).list();
        List<Source> sourceList = favourList.stream().map(sourceHasfavour ->
                sourceService.getById(sourceHasfavour.getSourceId())
        ).collect(Collectors.toList());
        return Result.success(sourceList);
    }
}
