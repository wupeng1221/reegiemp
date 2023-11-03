package com.wup.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wup.common.PageBean;
import com.wup.common.Result;
import com.wup.domain.Dish;
import com.wup.domain.DishFlavor;
import com.wup.dto.DishDto;
import com.wup.mapper.DishFlavorMapper;
import com.wup.mapper.DishMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("dish")
public class DishController {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @GetMapping("/page")
    public Result<PageBean<Dish>> list(@RequestParam("page") Integer pageNum,
                                       @RequestParam("pageSize") Integer pageSize,
                                       String name) {
        Page<Dish> page = new Page<Dish>(pageNum, pageSize);
        IPage<Dish> iPage = dishMapper.selectPage(page,
                new LambdaQueryWrapper<Dish>()
                        .like(name != null, Dish::getName, name));

        return Result.success(new PageBean<>(iPage.getTotal(), iPage.getRecords()));
    }

    @Transactional
    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto) {
        Long operateUser = (Long) httpServletRequest.getSession().getAttribute("id");
        //补全dish数据
        //dishID给dish设置id，给对应的口味设置对应的dishId
        dishDto.setCreateTime(LocalDateTime.now());
        dishDto.setUpdateTime(LocalDateTime.now());
        dishDto.setCreateUser(operateUser);
        dishDto.setUpdateUser(operateUser);
        dishMapper.insert(dishDto);
        Long dishDtoId = dishDto.getId();
        //补全dishFlavors数据,并写入数据库
        dishDto.getFlavors().forEach(
                flavor -> {
                    flavor.setDishId(dishDtoId);
                    flavor.setCreateTime(LocalDateTime.now());
                    flavor.setUpdateTime(LocalDateTime.now());
                    flavor.setCreateUser(operateUser);
                    flavor.setUpdateUser(operateUser);
                    dishFlavorMapper.insert(flavor);
                }
        );
        return Result.success("添加成功");
    }
    @GetMapping("/{id}")
    public Result<DishDto> getDishDtoById(@PathVariable Long id) {
        Dish dish = dishMapper.selectById(id);
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectList(
                new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, id));
        DishDto dishDto = BeanUtil.copyProperties(dish, DishDto.class);
        dishDto.setFlavors(dishFlavors);
        return Result.success(dishDto);
    }
    @Transactional
    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto) throws Exception{
        //修改dish,由于name是主键，直接修改会报错，先删除对应的dish数据新增，id可以保留，再生成也可以
        //id不进行修改,需要获取id进行数据库的增删，修改也可以
        //删除dish，以及dish对应的flavors
        dishMapper.deleteById(dishDto);
        dishFlavorMapper.delete(
                new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, dishDto.getId()));
        //新增dish
        //补全dish数据
        dishDto.setUpdateTime(LocalDateTime.now());
        //本次请求的请求对象id
        Long operateUser = (Long) httpServletRequest.getSession().getAttribute("id");
        dishDto.setUpdateUser(operateUser);
        dishMapper.update(
                BeanUtil.copyProperties(dishDto, Dish.class),
                new LambdaQueryWrapper<Dish>().eq(Dish::getId, dishDto.getId()));
        //将请求的新的flavor写入数据库
        dishDto.getFlavors().forEach(dishFlavor -> {
            dishFlavor.setDishId(dishDto.getId());
            dishFlavor.setCreateTime(LocalDateTime.now());
            dishFlavor.setUpdateTime(LocalDateTime.now());
            dishFlavor.setCreateUser(operateUser);
            dishFlavor.setUpdateUser(operateUser);
            dishFlavorMapper.insert(dishFlavor);
        });
        return Result.success(null);
    }
    @PostMapping("status/{newStatus}")
    public Result<String> updateStatus(@PathVariable Integer newStatus,
                                       @RequestParam("ids") Long[] ids) {
        dishMapper.update(null,
                new LambdaUpdateWrapper<Dish>().set(Dish::getStatus, newStatus)
                        .in(Dish::getId, ids));
        return Result.success("成功修改菜品状态");
    }
    @Transactional
    @DeleteMapping
    public Result<String> deleteByIds(@RequestParam("ids") Long[] ids) {
        //从dish中删除数据
        dishMapper.deleteBatchIds(Arrays.asList(ids));
        //从dishFlavor中删除对应的数据
        dishFlavorMapper.delete(
                new LambdaQueryWrapper<DishFlavor>().in(DishFlavor::getDishId, ids)
        );
        return Result.success(null);
    }
    @GetMapping("/list")
    public Result<List<Dish>> listByCategoryId(@RequestParam("categoryId") Long categoryId) {
        return Result.success(dishMapper.selectList(
                new LambdaQueryWrapper<Dish>().eq(Dish::getCategoryId, categoryId)
        ));
    }
}
