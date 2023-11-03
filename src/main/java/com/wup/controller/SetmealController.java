package com.wup.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.mail.MailUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wup.common.PageBean;
import com.wup.common.Result;
import com.wup.domain.Setmeal;
import com.wup.domain.SetmealDish;
import com.wup.dto.SetmealDto;
import com.wup.mapper.SetmealDishMapper;
import com.wup.mapper.SetmealMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("setmeal")
public class SetmealController {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @GetMapping("/page")
    public Result<PageBean<Setmeal>> list(@RequestParam("page") Integer pageNum,
                                          @RequestParam("pageSize") Integer pageSize,
                                          String name) {
        Page<Setmeal> page = new Page<Setmeal>(pageNum, pageSize);
        IPage<Setmeal> iPage = setmealMapper.selectPage(
                page,
                new LambdaQueryWrapper<Setmeal>()
                        .like(name != null, Setmeal::getName, name)
                );
        return Result.success(new PageBean<>(iPage.getTotal(), iPage.getRecords()));
    }
    @PostMapping("/status/{status}")
    public Result<String> updateStatus(@PathVariable Integer status, @RequestParam("ids") Long[] ids) {
        setmealMapper.update(null,
                new LambdaUpdateWrapper<Setmeal>()
                        .in(Setmeal::getStatus, Arrays.asList(ids))
                        .set(Setmeal::getStatus, status)
        );
        return Result.success(null);
    }
    @Transactional
    @DeleteMapping
    public Result<String> deleteSetmeal(@RequestParam("ids") Long[] ids) {
        setmealMapper.deleteBatchIds(Arrays.asList(ids));
        setmealMapper.delete(
                new LambdaQueryWrapper<Setmeal>()
                        .in(Setmeal::getId, Arrays.asList(ids))
        );
        return Result.success(null);
    }
    @Transactional
    @GetMapping("/{id}")
    public Result<SetmealDto> getSetmeal(@PathVariable Long id) {
        //setmeal
        Setmeal setmeal = setmealMapper.selectById(id);
        //setmealDish
        SetmealDto setmealDto = BeanUtil.copyProperties(setmeal, SetmealDto.class);
        List<SetmealDish> setmealDishes = setmealDishMapper.selectList(
                new LambdaQueryWrapper<SetmealDish>()
                        .eq(SetmealDish::getSetmealId, id)
        );
        setmealDto.setSetmealDishes(setmealDishes);
        return Result.success(setmealDto);
    }
    @Transactional
    @PostMapping
    public Result<String> saveSetmeal(@RequestBody SetmealDto setmealDto) {
        Long operateUser = (Long) httpServletRequest.getSession().getAttribute("id");
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDto, setmeal);
        //对setmeal进行数据补充
        setmeal.setCreateTime(LocalDateTime.now());
        setmeal.setUpdateTime(LocalDateTime.now());
        setmeal.setCreateUser(operateUser);
        setmeal.setUpdateUser(operateUser);
        //setmeal提交数据库
        setmealMapper.insert(setmeal);
        Long setmealId = setmeal.getId();
        //setmealDish数据补全
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
            setmealDish.setCreateTime(LocalDateTime.now());
            setmealDish.setUpdateTime(LocalDateTime.now());
            setmealDish.setCreateUser(operateUser);
            setmealDish.setUpdateUser(operateUser);

            setmealDishMapper.insert(setmealDish);
        });
        return Result.success(null);
    }
    @Transactional
    @PutMapping
    public Result<String> updateSetmeal(@RequestBody SetmealDto setmealDto) {
        Long operateUser = (Long) httpServletRequest.getSession().getAttribute("id");
        //获取setmealId进行数据库setmeal,setmealDish的删除
        setmealMapper.deleteById(setmealDto.getId());
        setmealDishMapper.delete(
                new LambdaQueryWrapper<SetmealDish>()
                       .eq(SetmealDish::getSetmealId, setmealDto.getId())
        );
        //重新填入数据
        //补充数据
        Setmeal setmeal = BeanUtil.copyProperties(setmealDto, Setmeal.class);
        setmeal.setUpdateTime(LocalDateTime.now());
        setmeal.setUpdateUser(operateUser);
        setmealMapper.insert(setmeal);
        Long newSetmealId = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(newSetmealId);
            setmealDish.setCreateTime(LocalDateTime.now());
            setmealDish.setUpdateTime(LocalDateTime.now());
            setmealDish.setUpdateUser(operateUser);
            setmealDish.setCreateUser(operateUser);
            setmealDishMapper.insert(setmealDish);
        });
        return Result.success(null);
    }
}
