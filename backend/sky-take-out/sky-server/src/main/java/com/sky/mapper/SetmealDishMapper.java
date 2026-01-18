package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询对应套餐id
     * @param dishIds
     * @return
     */
    // select setmeal_id from setmeal dish where dish_id in
    List<Long> getSetmealDishIds(List<Long> dishIds);

}
