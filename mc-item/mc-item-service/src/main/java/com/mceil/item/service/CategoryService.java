package com.mceil.item.service;

import com.mceil.common.vo.PageResult;
import com.mceil.item.bo.CategoryBo;
import com.mceil.item.bo.CategoryCount;
import com.mceil.item.pojo.Category;

import java.util.List;

public interface CategoryService {
    //根据父ID分页查询子分类
    PageResult<Category> queryCategoryListByPidPage(Integer page, Integer rows, Long pid);

    List<Category> queryCategoryListByPid(Long pid);


    //根据ids批量查询分类
    List<Category> queryByIds(List<Long> ids);

    //根据品牌id查询分类信息
    List<Category> queryCategoryBybId(long bid);

    //查询3级分类数据数据
    List<CategoryBo> queryCategoryList();

    Boolean saveCategory(Category category);

    void updateCategory(Category category);

    void deleteCategory(Long id);

    /**
     * 根据ids查询分类信息
     * @param asList
     * @return
     */
    List<String> queryNameByIds(List<Long> asList);

    /**
     * 更新分页状态
     * @param id
     */
    void updateCategoryStatus(Long id);

    Category queryCategoryById(Long id);

    /**
     * 获取二三级分类及其下商品个数
     * @return
     */
    List<CategoryCount> queryCategoryCountList();

    Boolean queryCategory(String name, Long parentId);
}
