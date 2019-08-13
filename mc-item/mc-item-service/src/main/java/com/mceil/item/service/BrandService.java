package com.mceil.item.service;

import com.mceil.common.vo.PageResult;
import com.mceil.item.bo.BrandBo;
import com.mceil.item.pojo.Brand;

import java.util.List;

public interface BrandService {
    //分页查询品牌
    PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String sortBy, Boolean desc, String keyword);

    //保存品牌信息
    void saveBrand(Brand brand, List<Long> cids);

    //根据ID查询品牌
    BrandBo queryById(Long id);

    //根据分类ID查询品牌
    List<Brand> queryBrandByCid(Long cid);

    //根据品牌ID批量查询品牌信息
    List<Brand> queryBrandByIds(List<Long> ids);

    //更新品牌信息
    void updateBrand(Brand brand, List<Long> cids);

    //删除品牌信息
    void deleteBrand(Long bid);

    PageResult<Brand> queryUserBrandByPage(Long uid, Integer page, Integer rows, String sortBy, Boolean desc, String keyword);

    void deleteUserBrand(Long bId);

    void saveUserBrand(Brand brand, List<Long> cids, Long uid);


    void batchInsertBrandCategory(Long bid, Long pid);


    List<Brand> queryBrandList();


}
