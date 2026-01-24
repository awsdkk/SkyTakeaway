package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;
     /**
      * 添加购物车
      * @param shoppingCartDTO
      */
     @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
         // 判断当前加入购物车菜品或套餐是否已经存在
         ShoppingCart shoppingCart = new ShoppingCart();
         // shoppingCartDTO -> shoppingCart
         BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
         // 从basecontext取出当前用户id 加入购物车的商品或套餐的用户id设置为当前用户id
         Long userId = BaseContext.getCurrentId();
         shoppingCart.setUserId(userId);

         List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

         // 如果存在对于这一条购物车数据条就加一
         if(list.size()>0 && list != null){
             ShoppingCart cart = list.get(0);
             cart.setNumber(cart.getNumber() + 1);
             shoppingCartMapper.updateNumberById(cart);
         }else{
             // 如果不存在要插入一条购物车数据
             // 判断一下是菜品还是套餐
             Long dishId = shoppingCartDTO.getDishId();
             if(dishId != null){
                 // 是菜品
                 // 从数据库中查询菜品信息
                 Dish dish = dishMapper.getById(dishId);
                 // 设置购物车的名称 图片 金额
                 shoppingCart.setName(dish.getName());
                 shoppingCart.setImage(dish.getImage());
                 shoppingCart.setAmount(dish.getPrice());
             }else{
                 // 是套餐
                 // 从数据库中查询套餐信息
                 Long setmealId = shoppingCartDTO.getSetmealId();
                 Setmeal setmeal = setmealMapper.getById(setmealId);
                 // 设置购物车的名称 图片 金额
                 shoppingCart.setName(setmeal.getName());
                 shoppingCart.setImage(setmeal.getImage());
                 shoppingCart.setAmount(setmeal.getPrice());
             }
             shoppingCart.setNumber(1);
             shoppingCart.setCreateTime(LocalDateTime.now());
             // 插入购物车
             shoppingCartMapper.insert(shoppingCart);
         }


    }

    /**
      * 查看购物车
      * @return
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        // 获取当前用户id
        Long userId = BaseContext.getCurrentId();
        // 构造对象
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        return list;
    }


}

