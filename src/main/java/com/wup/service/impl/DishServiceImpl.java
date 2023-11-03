package com.wup.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wup.domain.Dish;
import com.wup.service.DishService;
import com.wup.mapper.DishMapper;
import org.springframework.stereotype.Service;

/**
* @author brainwu
* @description 针对表【dish(菜品管理)】的数据库操作Service实现
* @createDate 2023-09-29 00:49:03
*/
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
    implements DishService{

}




