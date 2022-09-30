package com.example.controller;

import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.domain.Category;
import com.example.service.CategoryService;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * @author 24668
 */
@RestController
@RequestMapping(value = "/category")
public class CategoryController {


    @Autowired
    private CategoryService categoryService;

    @GetMapping("/getCatAll")
    public Result listCategorys() {
        List<Category> categorys = categoryService.list();

        return Result.success(categorys);
    }

    @GetMapping("/getName")
    public Result getName(Integer id){
        if (id == null){
            return Result.error(ResultCode.PARAM_IS_BLANK);
        }
        Category category = categoryService.getById(id);
        return Result.success(category.getCategoryName());
    }
}
