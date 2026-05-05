package com.sky.controller.admin;


import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags="套餐接口")
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @CacheEvict(cacheNames = "setmealCache",key ="#setmealDTO.categoryId")
    @PostMapping()
    @ApiOperation("新建套餐")
    public Result saveWithDish(@RequestBody SetmealDTO setmealDTO){
        log.info("新建套餐：{}",setmealDTO);
        setmealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("分页查询套餐")
    public Result<PageResult> getSetmealPage(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分类查询：{}",setmealPageQueryDTO);
        PageResult page=setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(page);
    }

    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    @DeleteMapping
    @ApiOperation("删除套餐")
    public Result deleteSetmeal(@RequestParam List<Long> ids){
        log.info("套餐删除：{}",ids);
        setmealService.delete(ids);
        return Result.success();

    }


    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getSetmealById(@PathVariable Long id){

       log.info("根据id查询套餐：{}",id);
       SetmealVO setmealVO=setmealService.getById(id);
       return Result.success(setmealVO);

    }
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    @PutMapping
    @ApiOperation("更改套餐信息")
    public Result updateSetmealById(@RequestBody SetmealDTO setmealDTO){
        log.info("更改套餐信息：{}",setmealDTO);
        setmealService.updateWithDish(setmealDTO);
        return Result.success();
    }
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    @PostMapping("/status/{status}")
    @ApiOperation("启售停售套餐")
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("启用禁用员工账号: {}{}", status, id);
        setmealService.startOrStop(status,id);
        return Result.success();
    }



}
