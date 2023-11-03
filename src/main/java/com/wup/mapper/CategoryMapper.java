package com.wup.mapper;

import com.wup.domain.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author brainwu
* @description 针对表【category(菜品及套餐分类)】的数据库操作Mapper
* @createDate 2023-09-28 22:15:26
* @Entity com.wup.domain.Category
*/
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}




