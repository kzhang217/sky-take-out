package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {



    List<Long> getSetmealIdsByDishId(List<Long> dishIds);

    void insertBatch(List<SetmealDish> dishes);

    @Delete("delete from setmeal_dish where setmeal_id=#{setmeal_id}")
    void deletBySetmealId(Long setmeal_id);

    void deletBySetmealIds(List<Long> ids);

    @Select("select * from  setmeal_dish where id=#{id}")
    List<SetmealDish> getByid(Long id);
}
