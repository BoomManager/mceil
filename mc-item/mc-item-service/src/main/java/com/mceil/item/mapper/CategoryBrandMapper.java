package com.mceil.item.mapper;


import com.mceil.item.pojo.CategoryBand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

public interface CategoryBrandMapper extends BaseMapper<CategoryBand>, InsertListMapper<CategoryBand> {
    @Select("select * from tb_category_brand where brand_id = #{bid}")
    List<CategoryBand> queryCategoryBandBybid(@Param("bid") Long bid);

}
