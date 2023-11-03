package com.wup.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.wup.common.PageBean;
import com.wup.common.Result;
import com.wup.domain.Employee;
import com.wup.mapper.EmployeeMapper;
import com.wup.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private HttpServletRequest request;
    @RequestMapping("/login")
    public Result<Object> login(@RequestBody Employee employee) {
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee byUsername = employeeMapper.selectOne(lambdaQueryWrapper);
        Employee byUsernameAndPassword = null;
        if (byUsername != null) {
            byUsernameAndPassword = employeeMapper.selectOne(
                    new LambdaQueryWrapper<Employee>()
                            .eq(Employee::getUsername, employee.getUsername())
                            .eq(Employee::getPassword, DigestUtils.md5DigestAsHex(employee.getPassword().getBytes()))
            );
        }

        if (byUsername == null) {
            return Result.error("用户名不存在");
        } else if(byUsernameAndPassword == null) {
            return Result.error("密码错误");
        } else if (byUsernameAndPassword.getStatus() != 1) {
            return Result.error("该用户已被禁止登录");
        } else {
            Long id = byUsernameAndPassword.getId();
            String name = byUsernameAndPassword.getName();
            String username = byUsernameAndPassword.getUsername();
            Map<String, Object> claims = new HashMap<>();
            claims.put("name", name);
            claims.put("username", username);
            String jwt = JwtUtils.generateJwt(claims);
            request.getSession().setAttribute("id", id);
            request.getSession().setAttribute("jwt", jwt);
            log.info("用户-{}-登录", name);
            return Result.success(byUsernameAndPassword);
        }
    }
    @GetMapping("/{id}")
    public Result findById(@PathVariable Long id) {
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Employee::getId, id);
        Employee employee = employeeMapper.selectOne(lambdaQueryWrapper);
        return Result.success(employee);
    }
    @PutMapping
    public Result updateAndModifyStatus(@RequestBody Employee employee) {
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser((Long) request.getSession().getAttribute("id"));
        employeeMapper.update(employee, new QueryWrapper<Employee>().lambda().eq(Employee::getId, employee.getId()));
        return Result.success(null);
    }
    @GetMapping("/page")
    public Result<Object> page(@RequestParam("page") Integer pageNum,
                               @RequestParam("pageSize") Integer pageSize,
                               String name) {
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name != null, Employee::getName, name);

        Page<Employee> page = new Page<>(pageNum, pageSize);
        IPage<Employee> iPage = employeeMapper.selectPage(page, lambdaQueryWrapper);
        return Result.success(new PageBean<>(iPage.getTotal(), iPage.getRecords()));
    }
    @PostMapping("/logout")
    public Result<String> logout() {
        request.getSession().removeAttribute("jwt");
        return Result.success("退出成功");
    }
    @PostMapping
    public Result<String> add(@RequestBody Employee employee) {
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        employee.setCreateUser((Long) request.getSession().getAttribute("id"));
        employee.setUpdateUser((Long) request.getSession().getAttribute("id"));
        employeeMapper.insert(employee);
        return Result.success("新增员工成功");
    }
}

