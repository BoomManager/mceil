package com.mceil.item.mapper;

import com.mceil.common.mapper.BaseMapper;
import com.mceil.item.pojo.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends BaseMapper<Brand> {
    /**
     * 新增商品分类和品牌中间表数据
     * @param cid 商品分类id
     * @param bid 品牌id
     * @return
     */
    @Insert("INSERT INTO tb_category_brand (category_id, brand_id) VALUES (#{cid},#{bid})")
    int insertCategoryBrand(@Param("cid") Long cid ,@Param("bid") Long bid);

    @Select("SELECT b.* FROM tb_category_brand cb INNER JOIN tb_brand b ON b.id =cb.brand_id WHERE cb.category_id =#{cid}")
    List<Brand> queryByCategoryId(@Param("cid") Long cid);

    @Delete("delete from tb_category_brand where brand_id = #{bid}")
    int deleteCategoryBrandBybid(@Param("bid") Long bid);

}
