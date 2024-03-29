package com.example.controller.source;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.annotation.Authentication;
import com.example.annotation.RepeatSubmit;
import com.example.constant.AuthConstant;
import com.example.constant.Result;
import com.example.domain.dao.Source;
import com.example.domain.dao.SourceHasthumb;
import com.example.service.SourceHasthumbService;
import com.example.service.SourceService;
import com.example.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("source")
@Api(tags = "资源收藏操作接口")
public class SourceHasthumbController {

    @Resource
    private SourceHasthumbService sourceHasthumbService;

    @Resource
    private SourceService sourceService;

    @PostMapping("thumb")
    @RepeatSubmit
    @Authentication(role = AuthConstant.USER)
    @ApiOperation(value = "点赞或取消点赞指定资源")
    public Result<Integer> favour(Integer targetId) {
        QueryWrapper<SourceHasthumb> queryWrapper = new QueryWrapper<>();
        String currentUser = UserUtils.getCurrentUser();
        queryWrapper.eq("user_id", currentUser);
        queryWrapper.eq("source_id", targetId);
        SourceHasthumb one = sourceHasthumbService.getOne(queryWrapper);
        Source source = sourceService.getById(targetId);
        if (one == null) {
            //添加点赞关系
            sourceHasthumbService.save(SourceHasthumb.builder().userId(currentUser).sourceId(targetId).build());
            source.setThumbNum(source.getThumbNum() + 1);
            sourceService.updateById(source);
            return Result.success(1);
        } else {
            //删除点赞关系
            sourceHasthumbService.remove(queryWrapper);
            source.setThumbNum(source.getThumbNum() - 1);
            sourceService.updateById(source);
            return Result.success(-1);
        }
    }
}
