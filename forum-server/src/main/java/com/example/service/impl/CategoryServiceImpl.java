package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domain.Category;
import com.example.service.CategoryService;
import com.example.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

/**
* @author 24668
* @description 针对表【tb_category】的数据库操作Service实现
* @createDate 2022-09-27 15:27:10
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

}




