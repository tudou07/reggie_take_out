package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    public CategoryService categoryService;

    //新增分类
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("新增分类...");
        if (categoryService.save(category))
            return R.success("新增分类成功");
        return R.error("新增分类失败");
    }

    //分页查询
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        log.info("分类分页查询...");
        Page<Category> categoryPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Category::getUpdateTime);
        categoryService.page(categoryPage,wrapper);
        return R.success(categoryPage);
    }

    //删除分类
    @DeleteMapping
    public R<String> delete(Long ids){
//        categoryService.removeById(ids)
        categoryService.remove(ids);
        return R.success("分类信息删除成功了。。。。");
    }

    //修改分类
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("分类修改》》》");
        categoryService.updateById(category);
        return R.success("分类修改成功");
    }

    //查询菜品分类
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        log.info("查询菜品分类,接收的参数是: {}",category);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }

}
