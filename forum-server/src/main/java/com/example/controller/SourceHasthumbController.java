package com.example.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.domain.Source;
import com.example.domain.SourceHasfavour;
import com.example.domain.SourceHasthumb;
import com.example.domain.User;
import com.example.service.SourceHasthumbService;
import com.example.service.SourceService;
import com.example.service.UserService;
import com.example.utils.UserUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

/**
 * @author 24668
 */
@RestController
@RequestMapping("source")
public class SourceHasthumbController {

    @Resource
    private SourceHasthumbService sourceHasthumbService;

    @Resource
    private UserService userService;

    @Resource
    private SourceService sourceService;

    /**
     * 点赞操作
     */
    @PostMapping("thumb")
    public Result favour(Integer targetId){
        QueryWrapper<SourceHasthumb> queryWrapper = new QueryWrapper<>();
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
        SourceHasthumb one = sourceHasthumbService.getOne(queryWrapper);
        Source source = sourceService.getById(targetId);
        if (one == null){
            //添加点赞关系
            sourceHasthumbService.save(new SourceHasthumb(currentUser,targetId));
            source.setThumbNum(source.getThumbNum()+1);
            sourceService.updateById(source);
            return Result.success(1);
        }else {
            //删除点赞关系
            sourceHasthumbService.remove(queryWrapper);
            source.setThumbNum(source.getThumbNum()-1);
            sourceService.updateById(source);
            return Result.success(-1);
        }
    }
}
