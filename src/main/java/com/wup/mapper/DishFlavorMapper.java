package com.wup.mapper;

import com.wup.domain.DishFlavor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author brainwu
* @description 针对表【dish_flavor(菜品口味关系表)】的数据库操作Mapper
* @createDate 2023-09-29 00:59:37
* @Entity com.wup.domain.DishFlavor
*/
@Mapper
public interface DishFlavorMapper extends BaseMapper<DishFlavor> {

}




