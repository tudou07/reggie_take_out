package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        if (emp == null)
            return R.error("登陆失败");
        if (!emp.getPassword().equals(password))
            return R.error("登陆失败");
        if (emp.getStatus() == 0)
            return R.error("账号已禁用！");
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");
    }

    //新增员工
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
//        LocalDateTime now = LocalDateTime.now();
//        employee.setCreateTime(now);
//        employee.setUpdateTime(now);
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        log.info("新增员工信息:{}",employee.toString());
        if (employeeService.save(employee))
            return R.success("新增员工成功");
        return R.error("新增员工失败");
    }

    //分页查询
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        Page pageInfo = new Page(page, pageSize);
        LambdaQueryWrapper<Employee> empLQW = new LambdaQueryWrapper<>();
        empLQW.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        empLQW.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo, empLQW);
        return R.success(pageInfo);
    }

    //启用、禁用员工账号
    @PutMapping
    public R<String> Update(HttpServletRequest request, @RequestBody Employee employee){
        log.info("启用/禁用员工...");
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
//        employee.setUpdateTime(LocalDateTime.now());
        if (employeeService.updateById(employee))
            return R.success("启用/禁用成功");
        return R.error("启用/禁用失败");
    }

    //编辑员工信息
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据ID查询员工信息...");
        Employee employee = employeeService.getById(id);
        if (employee != null)
            return R.success(employee);
        return R.error("没有查询到对应ID员工信息");
    }
}
