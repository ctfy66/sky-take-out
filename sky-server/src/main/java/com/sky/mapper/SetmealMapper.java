package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SetmealMapper {

    static void save(Setmeal setmeal) {
    }

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 查询dishId在套餐中出现的次数
     * @param dishId
     * @return
     */
    @Select("select count(id) from setmeal_dish where dish_id = #{dishId}")
    Integer countByDishId(Long dishId);

    /**
     * 新增套餐
     * @param setmeal
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @AutoFill(OperationType.INSERT)
    @Insert("insert into setmeal(name, category_id, price, status, description, image, create_time, update_time, create_user, update_user) " +
            "values(#{name}, #{categoryId}, #{price}, #{status}, #{description}, #{image}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insert(Setmeal setmeal);

    Page<DishVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    /**
     * 根据id删除套餐
     * @param setmealId
     */
    @Delete("delete from setmeal where id = #{id}")
    void deleteById(Long setmealId);

    /**
     * 修改套餐
     * @param setmeal
     */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);
}
