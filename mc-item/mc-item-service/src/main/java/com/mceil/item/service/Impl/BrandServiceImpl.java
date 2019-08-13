package com.mceil.item.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mceil.common.enums.ExceptionEnum;
import com.mceil.common.exception.McException;
import com.mceil.common.vo.PageResult;
import com.mceil.item.bo.BrandBo;
import com.mceil.item.mapper.BrandMapper;
import com.mceil.item.mapper.CategoryBrandMapper;
import com.mceil.item.mapper.CategoryMapper;
import com.mceil.item.mapper.UserBrandMapper;
import com.mceil.item.pojo.Brand;
import com.mceil.item.pojo.Category;
import com.mceil.item.pojo.CategoryBand;
import com.mceil.item.pojo.UserBrand;
import com.mceil.item.service.BrandService;
import com.mceil.item.service.CategoryService;
import com.sun.xml.internal.bind.v2.runtime.output.SAXOutput;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private UserBrandMapper userBrandMapper;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryBrandMapper categoryBrandMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String sortBy, Boolean desc, String keyword) {
        //分页
        PageHelper.startPage(page, rows);
        //过滤
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(keyword)) {
            //过滤条件
            criteria.andLike("name", "%" + keyword + "%")
                    .orEqualTo("letter", keyword.toUpperCase());
        }
        //排序
        if (StringUtils.isNotBlank(sortBy)) {
            example.setOrderByClause(sortBy + (desc ? " DESC" : " ASC"));
            //这里id ASC之间要有空格隔开，要不然分页助手会识别不了
        }
        //查询
        List<Brand> list = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            throw new McException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        //解析分页结果
        PageInfo<Brand> info = new PageInfo<>(list);

        return new PageResult<>(info.getTotal(), list);
    }

    //TODO 厂家完成新增品牌，原方法是否删除
    @Transactional
    @Override
    public void saveBrand(Brand brand, List<Long> cids) {
        //新增品牌
        brand.setId(null);
        int count = brandMapper.insertSelective(brand);

        if (count != 1) {
            throw new McException(ExceptionEnum.BRAND_SAVE_ERROR);
        }
        //新增中间表
        for (Long cid : cids) {
            count = brandMapper.insertCategoryBrand(cid, brand.getId());
            if (count != 1) {
                throw new McException(ExceptionEnum.CATEGORY_BRAND_SAVE_ERROR);
            }
        }
    }

    @Override
    public BrandBo queryById(Long id) {
        Brand brand = brandMapper.selectByPrimaryKey(id);
        BrandBo brandBo = new BrandBo();
        BeanUtils.copyProperties(brand,brandBo);
        List<Category> list = categoryService.queryCategoryBybId(id);
        List<String> cname = new ArrayList<>();
        if(!CollectionUtils.isEmpty(list)){
                for (Category category : list) {
                    if(category.getLevel().longValue() == 1L){
                        brandBo.setCid1(category.getId());
                    }else if(category.getLevel().longValue() == 2L){
                        brandBo.setCid2(category.getId());
                    }
                    if(category.getLevel().longValue() == 3L){
                        brandBo.setCid3(category.getId());
                    }
                    //cname.add(category.getName());
                }
            }
        //brandBo.setCname(StringUtils.join(cname,"-"));
        if (brand == null) {
            throw new McException((ExceptionEnum.BRAND_NOT_FOUND));
        }
        return brandBo;
    }

    @Override
    public List<Brand> queryBrandByCid(Long cid) {
        List<Brand> list = brandMapper.queryByCategoryId(cid);
        if (CollectionUtils.isEmpty(list)) {
            throw new McException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return list;
    }

    @Override
    public List<Brand> queryBrandByIds(List<Long> ids) {
        List<Brand> brands = brandMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(brands)) {
            throw new McException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brands;
    }

    @Override
    @Transactional
    public void updateBrand(Brand brand, List<Long> cids) {
        if (brand.getId() == null) {
            throw new McException(ExceptionEnum.BRAND_UPDATE_ERROR);
        }
        brandMapper.updateByPrimaryKeySelective(brand);
        brandMapper.deleteCategoryBrandBybid(brand.getId());
        for (Long cid : cids) {
            int count = 0;
            count = brandMapper.insertCategoryBrand(cid, brand.getId());
            if (count != 1) {
                throw new McException(ExceptionEnum.CATEGORY_BRAND_SAVE_ERROR);
            }
        }
    }

    //TODO 是否删除
    @Transactional
    @Override
    public void deleteBrand(Long bid) {
        Brand brand = brandMapper.selectByPrimaryKey(bid);
        if (brand == null) {
            throw new McException(ExceptionEnum.BRAND_DELETE_ERROR);
        }

        int count = brandMapper.deleteByPrimaryKey(brand.getId());
        if (count == 0) {
            throw new McException(ExceptionEnum.BRAND_DELETE_ERROR);
        }
    }


    //*******************厂家***************************//
    @Override
    public PageResult<Brand> queryUserBrandByPage(Long uid, Integer page, Integer rows, String sortBy, Boolean desc, String keyword) {
        UserBrand userBrand = new UserBrand();
        userBrand.setUid(uid);
        List<UserBrand> userBrands = userBrandMapper.select(userBrand);
        List<Long> ids = userBrands.stream().map(UserBrand::getBid).collect(Collectors.toList());
        List<Brand> brandList = brandMapper.selectByIdList(ids);
        PageHelper.startPage(page, rows);
        //解析分页结果
        PageInfo<Brand> info = new PageInfo<>(brandList);
        info.setEndRow(rows);

        return new PageResult<>(info.getTotal(), brandList);
    }

    @Transactional
    @Override
    public void saveUserBrand(Brand brand, List<Long> cids, Long uid) {
        //新增品牌
        brand.setId(null);
        int count = brandMapper.insert(brand);

        if (count != 1) {
            throw new McException(ExceptionEnum.BRAND_SAVE_ERROR);
        }
        //新增中间表
        for (Long cid : cids) {
            count = brandMapper.insertCategoryBrand(cid, brand.getId());
            if (count != 1) {
                throw new McException(ExceptionEnum.CATEGORY_BRAND_SAVE_ERROR);
            }
        }
        System.out.println(uid);
        //新增用户关系表
        UserBrand userBrand = new UserBrand();
        userBrand.setUid(uid);
        userBrand.setBid(brand.getId());
        userBrandMapper.insert(userBrand);
    }

    @Override
    public void batchInsertBrandCategory(Long bid, Long pid) {
        List<Category> categoryList = categoryService.queryCategoryListByPid(pid);
        List<CategoryBand> categoryBandList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(categoryList)){
            for (Category category : categoryList) {
                CategoryBand categoryBand = new CategoryBand();
                categoryBand.setBrand_id(1L);
                categoryBand.setCategory_id(category.getId());
                categoryBandList.add(categoryBand);
            }
        }
        categoryBrandMapper.insertList(categoryBandList);
    }



    @Override
    public List<Brand> queryBrandList() {
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status",1).andEqualTo("isDelete",1);
        List<Brand> list = brandMapper.selectByExample(example);

        return list;
    }


    @Transactional
    @Override
    public void deleteUserBrand(Long bid) {
        Brand brand = brandMapper.selectByPrimaryKey(bid);
        if (brand == null) {
            throw new McException(ExceptionEnum.BRAND_DELETE_ERROR);
        }
        int count = brandMapper.deleteCategoryBrandBybid(brand.getId());
        if (count == 0) {
            throw new McException(ExceptionEnum.BRAND_DELETE_ERROR);
        }
        count = brandMapper.deleteByPrimaryKey(brand.getId());
        if (count == 0) {
            throw new McException(ExceptionEnum.BRAND_DELETE_ERROR);
        }
        count = userBrandMapper.deleteByPrimaryKey(bid);
        if (count == 0) {
            throw new McException(ExceptionEnum.BRAND_DELETE_ERROR);
        }
    }

}
