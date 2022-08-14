package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    //保存套餐
    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("保存套餐setmealDto：{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("保存套餐成功");
    }

    //分页查询套餐
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("分页查询套餐.....");
        Page<SetmealDto> setmealDtoPage = setmealService.pageWithCategoryName(page, pageSize, name);
        return R.success(setmealDtoPage);
    }

    //删除套餐
    @DeleteMapping
    public R<String> remove(@RequestParam List<Long> ids){
        log.info("删除套餐：{}",ids);
        setmealService.removeWithDish(ids);
        return R.success("套餐删除成功");
    }

    //启售、停售
    @PostMapping("/status/{num}")
    public R<String> changeStatus(@PathVariable int num, @RequestParam List<Long> ids){
        log.info("启售、停售。。。状态：{},{}",num,ids);
        setmealService.changeStatus(num,ids);
        return R.success("状态修改成功");
    }

    //修改页面加载
    @GetMapping("/{id}")
    public R<SetmealDto> change(@PathVariable Long id){
        log.info("修改页面数据加载，套餐id：{}",id);
        SetmealDto setmealDto = setmealService.change(id);
        return R.success(setmealDto);
    }

    //修改提交
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        log.info("修改页面提交，参数：{}",setmealDto);
        if (setmealService.updateWithDish(setmealDto))
            return R.success("套餐修改成功");
        return R.error("套餐修改失败");
    }

    //条件查询套餐
    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal){
        log.info("条件查询套餐。。。条件为：{}",setmeal);
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        wrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(wrapper);
        return R.success(list);
    }

    //查询套餐菜品信息
    @GetMapping("/dish/{id}")
    public R<List<SetmealDish>> listDish(@PathVariable Long id){
        log.info("查询套餐菜品详情：套餐id：{}",id);
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(id != null, SetmealDish::getSetmealId, id);
        wrapper.orderByDesc(SetmealDish::getUpdateTime);
        List<SetmealDish> list = setmealDishService.list(wrapper);
        return R.success(list);
    }

}
