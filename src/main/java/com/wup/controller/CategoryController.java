package com.wup.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wup.common.PageBean;
import com.wup.common.Result;
import com.wup.domain.Category;
import com.wup.mapper.CategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private HttpServletRequest request;
    @GetMapping("/page")
    public Result<PageBean<Category>> list(@RequestParam("page") Integer pageNum,
                                           @RequestParam("pageSize") Integer pageSize) {
        Page<Category> page = new Page<Category>(pageNum, pageSize);
        IPage<Category> iPage = categoryMapper.selectPage(page, new QueryWrapper<Category>());
        return Result.success(new PageBean<Category>(iPage.getTotal(), iPage.getRecords()));
    }
    @PostMapping
    public Result<Object> save(@RequestBody Category category) {
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setCreateUser((Long) request.getSession().getAttribute("id"));
        category.setUpdateUser((Long) request.getSession().getAttribute("id"));
        categoryMapper.insert(category);
        return Result.success(null);
    }
    @DeleteMapping()
    public Result<Object> delete(@RequestParam("ids") Long id) {
        categoryMapper.deleteById(id);
        return Result.success(null);
    }
    @PutMapping
    public Result<Object> update(@RequestBody Category category) {
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser((Long) request.getSession().getAttribute("id"));
        categoryMapper.update(category, new LambdaQueryWrapper<Category>().eq(Category::getId, category.getId()));
        return Result.success(null);
    }
    @GetMapping("/list")
    public Result<List<Category>> listByType(
            @RequestParam(name = "type", defaultValue = "1")
            Integer type) {
        List<Category> categories = categoryMapper.selectList(new LambdaQueryWrapper<Category>().eq(Category::getType, type));
        return Result.success(categories);
    }
}
