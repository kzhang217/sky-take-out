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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.awt.*;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {


    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long id=BaseContext.getCurrentId();
        shoppingCart.setUserId(id);
        List<ShoppingCart> list=shoppingCartMapper.list(shoppingCart);

        if(list != null && list.size()>0){
            ShoppingCart shoppingCart1=list.get(0);
            shoppingCart1.setNumber(shoppingCart1.getNumber()+1);
            shoppingCartMapper.update(shoppingCart1);
        }else{

            Long dishId=shoppingCartDTO.getDishId();
            if(dishId != null){
                Dish dish=dishMapper.getByid(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }else{
                Setmeal setmeal=setmealMapper.getByid(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    @Override
    public List<ShoppingCart> showShoppingCart(Long useId) {
        ShoppingCart shoppingCart=new ShoppingCart();
        shoppingCart.setUserId(useId);
        List<ShoppingCart> carts=shoppingCartMapper.list(shoppingCart);
        return carts;
    }

    @Override
    public void cleanShoppingCart() {
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());

    }

    @Override
    public void subByID(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long id=BaseContext.getCurrentId();
        shoppingCart.setUserId(id);
        List<ShoppingCart> list=shoppingCartMapper.list(shoppingCart);
        shoppingCart=list.get(0);
        Integer number = shoppingCart.getNumber();

        if(number > 1){
            shoppingCart.setNumber(number-1);
            shoppingCartMapper.update(shoppingCart);
        }else{
            shoppingCartMapper.deleteById(shoppingCart);
        }
    }
}
