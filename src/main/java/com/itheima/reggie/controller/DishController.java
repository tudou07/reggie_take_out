package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    //菜品保存
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("菜品保存成功");
    }

    //分页查询
    @GetMapping("/page")
    public R<Page> page(String name, int page, int pageSize){
        log.info("菜品分页查询");
        Page<Dish> dishPage = new Page<Dish>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.like(name != null,Dish::getName, name);
        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(dishPage,dishLambdaQueryWrapper);
        //对象拷贝,除去recors数据不拷贝
        BeanUtils.copyProperties(dishPage, dishDtoPage,"records");
        List<Dish> records = dishPage.getRecords();
        List<DishDto> list = records.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    //修改页面查询
    @GetMapping("/{id}")
    public R<DishDto> getByIdWithFlavor(@PathVariable Long id){
        log.info("修改菜品查询。。。");
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    //修改菜品保存
    @PutMapping
    public R<String> change(@RequestBody DishDto dishDto){
        log.info("修改菜品提交成功...");
        dishService.updateWithFlavor(dishDto);

        //清理所有菜品缓存数据
//        Set<Object> keys = redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);
        //清理某个分类下菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("菜品信息修改成功");
    }

    //根据分类ID查询
    @GetMapping("/list")
    public R<List<DishDto>> listDish(Setmeal setmeal){

        List<DishDto> dtoList = null;
        //动态构造key
        String key = "dish_" + setmeal.getCategoryId() + "_" + setmeal.getStatus();
        //先从redis中获取缓存数据
        dtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (dtoList != null){
            //如果缓存中有，则返回
            return R.success(dtoList);
        }
        //如果缓存中没有，则查询数据库
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1);
        List<Dish> list = dishService.list(queryWrapper);
        dtoList = list.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DishFlavor::getDishId, item.getId());
            List<DishFlavor> flavorList = dishFlavorService.list(wrapper);
            dishDto.setFlavors(flavorList);
            return dishDto;
        }).collect(Collectors.toList());
        redisTemplate.opsForValue().set(key, dtoList, 60, TimeUnit.MINUTES);
        return R.success(dtoList);
    }

}
