package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.OrdersService;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    //提交订单，去支付
    @PostMapping("/submit")
    public R<String > submit(@RequestBody Orders orders){
        log.info("去支付订单。。。订单参数为：{}",orders);
        ordersService.submit(orders);
        return R.success("订单提交成功");
    }

    //用户分页查询订单
    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize){
        log.info("用户分页查询订单。。。查询信息：page={},pageSize={}",page,pageSize);
        Long userId = BaseContext.getCurrentId();
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userId != null, Orders::getUserId, userId);
        wrapper.orderByDesc(Orders::getCheckoutTime);
        ordersService.page(ordersPage, wrapper);
        return R.success(ordersPage);
    }

    //员工分页查询订单
    @GetMapping("/page")
    public R<Page> employeePage(int page, int pageSize, String number,
                                @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date beginTime,
                                @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date endTime){
        log.info("员工分页查询订单。。。查询信息：page={},pageSize={}, 订单号：{},开始时间：{},结束时间：{}",
                page, pageSize, number, beginTime, endTime);
        /*Long userId = BaseContext.getCurrentId();
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userId != null, Orders::getUserId, userId);
        wrapper.orderByDesc(Orders::getCheckoutTime);
        ordersService.page(ordersPage, wrapper);
        return R.success(ordersPage);*/

        return null;
    }
}
