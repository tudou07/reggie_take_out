package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    //添加到购物车
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("添加到购物车，被添加数据：{}",shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<ShoppingCart>();
        wrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());
        Long dishId = shoppingCart.getDishId();
        //判断是菜品还是套餐
        if (dishId != null){
            wrapper.eq(ShoppingCart::getDishId, dishId);
        }else {
            wrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(wrapper);
        //判断菜品或套餐是否已经在购物车中
        if (cartServiceOne != null){
            cartServiceOne.setNumber(cartServiceOne.getNumber() + 1);
            shoppingCartService.updateById(cartServiceOne);
        }else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }

        return R.success(cartServiceOne);
    }

    //查询购物车
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查询购物车清单。。。");
        List<ShoppingCart> shoppingCarts = shoppingCartService.listCart();
        return R.success(shoppingCarts);
    }

    //清空购物车
    @DeleteMapping("/clean")
    public R<String> clear(){
        shoppingCartService.clearCart();
        return R.success("清空购物车成功");
    }

}
