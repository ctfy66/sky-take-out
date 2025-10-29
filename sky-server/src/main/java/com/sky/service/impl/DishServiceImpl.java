package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        // 封装为Dish entity 和多个DishFlavor entity
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 保存Dish entity 到数据库
        dishMapper.insert(dish);
        // 保存多个DishFlavor entity 到数据库
        for (DishFlavor dishFlavor : dishDTO.getFlavors()) {
            Long dishId = dish.getId();
            dishFlavor.setDishId(dishId);
            dishFlavorMapper.insert(dishFlavor);
        }
    }

    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        long total = page.getTotal();
        List<DishVO> records = page.getResult();
        return new PageResult(total, records);
    }

    @Transactional
    public void batchDelete(List<Long> ids) {
        for (Long id : ids) {
            // 查询setmeal表，若关联则抛出业务异常
            if (setmealMapper.countByDishId(id) > 0) {
                // 抛出业务异常
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
            // 查询菜品表，若为启售状态则抛出业务异常
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        // 批量删除菜品及其对应的口味数据
        dishMapper.deleteBatch(ids);
        dishFlavorMapper.deleteByDishIds(ids);
    }

    public DishVO getByIdWithFlavor(Long id) {
        // 查询菜品基本信息
        Dish dish = dishMapper.getById(id);
        if (dish == null) {
            return null;
        }
        // 查询菜品对应的口味信息
        List<DishFlavor> flavors = dishFlavorMapper.listByDishId(id);
        // 封装为DishVO对象并返回
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        // 更新菜品基本信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        // 删除原有的口味数据
        Long dishId = dishDTO.getId();
        dishFlavorMapper.deleteByDishId(dishId);
        // 插入新的口味数据
        for (DishFlavor dishFlavor : dishDTO.getFlavors()) {
            dishFlavor.setDishId(dishId);
            dishFlavorMapper.insert(dishFlavor);
        }
    }

    @Override
    public void chageStatus(Integer status, Long id) {
        Dish dish = Dish.builder()
                .status(status).id(id).build();
        dishMapper.update(dish);
    }


}
