package com.example.controller.source;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.annotation.Authentication;
import com.example.constant.AuthConstant;
import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.domain.bo.QiNiuImage;
import com.example.domain.dao.Source;
import com.example.service.SourceService;
import com.example.utils.QiniuServiceImpl;
import com.example.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhw
 */
@RestController
@RequestMapping("source")
@Api(tags = "资源接口")
public class SourceController {

    @Resource
    private SourceService sourceService;

    @Resource
    QiniuServiceImpl qiniuService;

    @PostMapping("vo")
    @ApiOperation(value = "根据条件分页查询资源信息")
    public IPage<Source> pageVo(String type,String sort,Integer pageNo,Integer pageSize){
        return sourceService.pageVo(type,sort,pageNo,pageSize);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询资源信息")
    public Result queryById(@PathVariable Integer id){
        return sourceService.queryById(id);
    }

    @Authentication
    @PostMapping("getAll/{state}")
    @ApiOperation(value = "根据状态查询资源信息")
    public Result getAll(@PathVariable Integer state){
        List<Source> list = sourceService.lambdaQuery().eq(Source::getState,state).list();
        return Result.success(list);
    }

    @Authentication
    @PostMapping("/delete/{id}")
    @ApiOperation(value = "根据id删除资源")
    public Result deleteById(@PathVariable Integer id) {
        boolean flag = sourceService.removeById(id);
        return flag ? Result.success() : Result.error();
    }

    @Authentication
    @PostMapping("pass")
    @ApiOperation(value = "管理员审核资源")
    public Result pass(Integer id,Integer type) {
        return sourceService.pass(id,type);
    }

    @PostMapping("insert")
    @Authentication(role = AuthConstant.USER)
    @ApiOperation(value = "表单提交分享资源")
    public Result insert(@RequestBody @Valid Source source){
        String currentUser = UserUtils.getCurrentUser();
        source.setUserId(currentUser);
        boolean flag = sourceService.save(source);
        return flag ? Result.success() : Result.error();
    }

    @PostMapping("upload")
    @Authentication(role = AuthConstant.USER)
    @ApiOperation(value = "上传资源附件")
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
