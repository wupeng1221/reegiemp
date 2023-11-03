package com.wup.mapper;

import com.wup.domain.Dish;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author brainwu
* @description 针对表【dish(菜品管理)】的数据库操作Mapper
* @createDate 2023-09-29 00:49:03
* @Entity com.wup.domain.Dish
*/
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

}




