package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);
    Page<SetmealDto> pageWithCategoryName(int page, int pageSize, String name);
    void removeWithDish(List<Long> ids);
    void changeStatus(int num, List<Long> ids);
    //修改页面加载
    SetmealDto change(Long id);
    //修改提交
    boolean updateWithDish(SetmealDto setmealDto);
}
