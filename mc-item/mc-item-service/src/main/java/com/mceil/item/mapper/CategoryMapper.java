package com.mceil.item.mapper;

import com.mceil.item.pojo.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CategoryMapper extends Mapper<Category>, IdListMapper<Category,Long> {
    /**
     * 根据category id删除中间表相关数据
     * @param cid
     */
    @Delete("DELETE FROM tb_category_brand WHERE category_id = #{cid}")
    void deleteByCategoryIdInCategoryBrand(@Param("cid") Long cid);

    @Select("select * from tb_category where parent_id=0")
    List<Category> queryCategoryParent();
    /**
     * 根据id查名字
     * @param id
     * @return
     */
    @Select("SELECT name FROM tb_category WHERE id = #{id}")
    String queryNameById(Long id);
}
