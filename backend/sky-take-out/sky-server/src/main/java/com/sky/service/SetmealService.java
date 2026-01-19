package com.sky.service;

import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;

public interface SetmealService {

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDTO
     */
    void saveWithDish(SetmealDTO setmealDTO);

    /**
     * 新增套餐
     * @param setmeal
     */
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);
}