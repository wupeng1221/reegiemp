package com.wup.dto;


import com.wup.domain.Dish;
import com.wup.domain.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class DishDto extends Dish {
    private List<DishFlavor> flavors = new ArrayList<>();
    private String categoryName;
    private Integer copies;
}
