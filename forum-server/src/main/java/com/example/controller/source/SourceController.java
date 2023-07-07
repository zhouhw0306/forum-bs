package com.example.controller.source;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.annotation.Authentication;
import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.constant.SourceEnum;
import com.example.domain.bo.QiNiuImage;
import com.example.domain.dao.Source;
import com.example.domain.dao.SourceHasfavour;
import com.example.domain.dao.SourceHasthumb;
import com.example.domain.dao.User;
import com.example.service.SourceHasfavourService;
import com.example.service.SourceHasthumbService;
import com.example.service.SourceService;
import com.example.service.UserService;
import com.example.utils.QiniuServiceImpl;
import com.example.utils.UserUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    /**
     *  条件查
      */
    @PostMapping("vo")
    public IPage pageVo(String type,String sort,Integer pageNo,Integer pageSize){
        QueryWrapper<Source> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category", SourceEnum.findEnumByName(type));
        queryWrapper.eq("state", 1);
        queryWrapper.orderByDesc(sort);
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

    /**
     * 根据id查
     */
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

    /**
     * 查全部
     */
    @Authentication
    @PostMapping("getAll/{state}")
    public Result getALl(@PathVariable Integer state){
        List<Source> list = sourceService.query().eq("state",state).list();
        return Result.success(list);
    }

    /**
     * 根据id删除
     */
    @Authentication
    @PostMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        boolean flag = sourceService.removeById(id);
        return flag ? Result.success() : Result.error();
    }

    /**
     * 审核资源
     */
    @Authentication
    @PostMapping("pass")
    public Result pass(Integer id,Integer type) {
        return sourceService.pass(id,type);
    }

    @PostMapping("insert")
    public Result insert(@RequestBody Source source){
        String currentUser = UserUtils.getCurrentUser();
        if (currentUser == null){
            Result.error();
        }
        source.setUserId(currentUser);
        boolean flag = sourceService.save(source);
        return flag ? Result.success() : Result.error();
    }

    @Resource
    QiniuServiceImpl qiniuService;

    @PostMapping("upload")
    public Result upload(MultipartFile file) {

        Result r = new Result();
        try {
            String filePath = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String baseFolder = "source/" + filePath;

            QiNiuImage qiNiuImage = qiniuService.saveToQiNiu(file, baseFolder);

            r.setResultCode(ResultCode.SUCCESS);
            Map<String, Object> map = r.simple();
            map.put("fileUrl", qiNiuImage.getFileName());
            map.put("fileName", file.getOriginalFilename());
        } catch (Exception e) {
            r.setResultCode(ResultCode.UPLOAD_ERROR);
        }

        return r;
    }
}
