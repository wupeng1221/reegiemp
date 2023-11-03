package com.wup.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wup.domain.DishFlavor;
import com.wup.service.DishFlavorService;
import com.wup.mapper.DishFlavorMapper;
import org.springframework.stereotype.Service;

/**
* @author brainwu
* @description 针对表【dish_flavor(菜品口味关系表)】的数据库操作Service实现
* @createDate 2023-09-29 00:59:37
*/
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor>
    implements DishFlavorService{

}




