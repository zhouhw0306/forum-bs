package com.example.controller;

import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.domain.dao.Category;
import com.example.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * @author zhw
 */
@RestController
@RequestMapping(value = "/category")
@Api(tags = "文章类型接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/getCatAll")
    @ApiOperation(value = "获取文章的全部类型")
    public Result listCategorys() {
        List<Category> categorys = categoryService.list();
        return Result.success(categorys);
    }

    @GetMapping("/getName")
    @ApiOperation(value = "根据id获取类型名")
    public Result getName(Integer id){
        Category category = categoryService.getById(id);
        return Result.success(category.getCategoryName());
    }
}
