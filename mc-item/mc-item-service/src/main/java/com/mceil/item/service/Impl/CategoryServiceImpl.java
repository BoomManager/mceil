package com.mceil.item.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mceil.common.enums.ExceptionEnum;
import com.mceil.common.exception.McException;
import com.mceil.common.vo.PageResult;
import com.mceil.item.bo.CategoryBo;
import com.mceil.item.bo.CategoryCount;
import com.mceil.item.mapper.CategoryBrandMapper;
import com.mceil.item.mapper.CategoryMapper;
import com.mceil.item.mapper.GoodsMapper;
import com.mceil.item.mapper.SpecMapper;
import com.mceil.item.pojo.*;
import com.mceil.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryBrandMapper categoryBrandMapper;

    @Autowired
    private SpecMapper specMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public PageResult<Category> queryCategoryListByPidPage(Integer page, Integer rows, Long pid) {
        //查詢條件，mapper會把對象中的非空屬性作爲查詢條件
        PageHelper.startPage(page,rows);
        List<Category> list = queryCategoryListByPid(pid);
        if (CollectionUtils.isEmpty(list)) {
            throw new McException(ExceptionEnum.CATEGORY_NOT_FOND);
        }
        //解析分页结果
        PageInfo<Category> info = new PageInfo<>(list);

        return new PageResult<>(info.getTotal(), list);

    }

    @Override
    public List<Category> queryCategoryListByPid(Long pid) {

        //查詢條件，mapper會把對象中的非空屬性作爲查詢條件
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        example.setOrderByClause(" sort ASC");
        criteria.andEqualTo("parentId",pid);
        List<Category> list = categoryMapper.selectByExample(example);
        return list;

    }

    @Override
    public List<Category> queryByIds(List<Long> ids) {
        List<Category> list = categoryMapper.selectByIdList(ids);
        return list;
    }

    @Override
    public List<Category> queryCategoryBybId(long bid) {
        List<CategoryBand> categoryBandList = categoryBrandMapper.queryCategoryBandBybid(bid);
        if(!CollectionUtils.isEmpty(categoryBandList)){
            List<Long> cids = categoryBandList.stream().map(CategoryBand::getCategory_id).collect(Collectors.toList());
            Example example = new Example(Category.class);
            example.createCriteria().andIn("id",cids);
            List<Category> list = categoryMapper.selectByExample(example);
            return list;
        }
        return null;
    }

    //查询2，3级分类信息
    @Override
    public List<CategoryBo> queryCategoryList() {
        List<CategoryBo> categoryBoList = new ArrayList<>();
        //构造顶级分类
        List<Category> categoryList = new ArrayList<>();
        Category category = new Category();
        category.setId(0L);
        categoryList.add(category);
        //获取所有一级分类
        List<Category> categoryListOne = getCategoryList(categoryList);
        //获取所有二级分类
        List<Category> categoryListTwo = getCategoryList(categoryListOne);
        //获取所有三级分类
        List<Category> categoryListThree = getCategoryList(categoryListTwo);
        //合并二级分类和三级分类
        List<CategoryBo> CategoryBoMager = magerCategoryList(categoryListTwo, categoryListThree);
        if (!CollectionUtils.isEmpty(categoryListOne)) {
            for (Category categoryOne : categoryListOne) {
                CategoryBo categoryBo = magerCategoryListAll(categoryOne, CategoryBoMager);
                categoryBoList.add(categoryBo);
            }
        }
        return categoryBoList;
    }

    @Override
    @Transactional
    public Boolean saveCategory(Category category) {
        /**
         * 将本节点插入到数据库中
         * 将此category的父节点的isParent设为true
         */
        //1.首先置id为null
        category.setId(null);
        if(category.getParentId().longValue() == 0L){
            category.setLevel(1L);
        }else{
            //获取父分类数据
            Category record = categoryMapper.selectByPrimaryKey(category.getParentId());
            if(record != null){
                //判断他的分类是否是一级分类
                if(record.getParentId().longValue() == 0L){
                    category.setLevel(2L);
                }else{
                    category.setLevel(3L);
                }
            }
        }
        //判断分类名称是否存在
        Boolean flag = queryCategory(category.getName(),category.getParentId());
        if(!flag){
            //2.保存
            this.categoryMapper.insert(category);
            //3.修改父节点
            Category parent = new Category();
            parent.setId(category.getParentId());
            parent.setIsParent(true);
            this.categoryMapper.updateByPrimaryKeySelective(parent);
            return true;
        }
        return false;


    }

    @Override
    @Transactional
    public void updateCategory(Category category) {
        this.categoryMapper.updateByPrimaryKeySelective(category);
    }

    @Override
    public void deleteCategory(Long id) {
        /**
         * 先根据id查询要删除的对象，然后进行判断
         * 如果是父节点，那么删除所有附带子节点,然后维护中间表
         * 如果是子节点，那么只删除自己,然后判断父节点孩子的个数，如果孩子不为0，则不做修改；如果孩子个数为0，则修改父节点isParent
         * 的值为false,最后维护中间表
         */
        Category category=this.categoryMapper.selectByPrimaryKey(id);
        if(category.getIsParent()){
            //1.查找所有叶子节点
            List<Category> list = new ArrayList<>();
            queryAllLeafNode(category,list);

            //2.查找所有子节点
            List<Category> list2 = new ArrayList<>();
            queryAllNode(category,list2);

            //3.删除tb_category中的数据,使用list2
            List<Long> specIds = new ArrayList<>();
            for (Category c:list2){
                this.categoryMapper.delete(c);

                Spec record = new Spec();
                record.setCid(c.getId());
                Spec spec = specMapper.selectOne(record);
                if(spec != null){
                    specIds.add(spec.getId());
                }
            }
            //维护分类规格模板参数表
            specMapper.deleteByIdList(specIds);
            //4.维护中间表
            for (Category c:list){
                this.categoryMapper.deleteByCategoryIdInCategoryBrand(c.getId());
            }

        }else {
            //1.查询此节点的父亲节点的孩子个数 ===> 查询还有几个兄弟
            Example example = new Example(Category.class);
            example.createCriteria().andEqualTo("parentId",category.getParentId());
            List<Category> list=this.categoryMapper.selectByExample(example);
            if(list.size()!=1){
                //有兄弟,直接删除自己
                this.categoryMapper.deleteByPrimaryKey(category.getId());

                //维护中间表
                this.categoryMapper.deleteByCategoryIdInCategoryBrand(category.getId());
            }
            else {
                //已经没有兄弟了
                this.categoryMapper.deleteByPrimaryKey(category.getId());

                Category parent = new Category();
                parent.setId(category.getParentId());
                parent.setIsParent(false);
                this.categoryMapper.updateByPrimaryKeySelective(parent);
                //维护中间表
                this.categoryMapper.deleteByCategoryIdInCategoryBrand(category.getId());
            }
        }
    }

    @Override
    public List<String> queryNameByIds(List<Long> asList) {
        List<String> names = new ArrayList<>();
        if (asList != null && asList.size() !=0){
            for (Long id : asList) {
                names.add(this.categoryMapper.queryNameById(id));
            }
        }
        return names;
    }

    @Override
    public void updateCategoryStatus(Long id) {
        //根据id获取分类数据
        Category record = categoryMapper.selectByPrimaryKey(id);
        if(record.getStatus()){
            record.setStatus(false);
            categoryMapper.updateByPrimaryKeySelective(record);
        }else{
            record.setStatus(true);
            categoryMapper.updateByPrimaryKeySelective(record);
        }

    }

    @Override
    public Category queryCategoryById(Long id) {
        return categoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<CategoryCount> queryCategoryCountList() {
        List<CategoryCount> list = new ArrayList<>();
        //获取所有的二级分类
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("level",2);
        List<Category> categoryList = categoryMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(categoryList)){
            for (Category category : categoryList) {
                List<CategoryCount> listThree = new ArrayList<>();
                List<Category> categories = queryCategoryListByPid(category.getId());
                if(!CollectionUtils.isEmpty(categories)){
                    for (Category category1 : categories) {
                        CategoryCount categoryChild = new CategoryCount();
                        categoryChild.setCount(queryGoodsCount(category1.getId(),3));
                        categoryChild.setName(category1.getName());
                        categoryChild.setId(category1.getId());
                        listThree.add(categoryChild);
                    }
                }
                CategoryCount categoryCount = new CategoryCount();
                categoryCount.setId(category.getId());
                categoryCount.setName(category.getName());
                categoryCount.setCount(queryGoodsCount(category.getId(),2));
                categoryCount.setCategoryCounts(listThree);
                list.add(categoryCount);
            }
        }
        return list;
    }

    @Override
    public Boolean queryCategory(String name, Long parentId) {
        Boolean flag = true;
        //判断该名称是否存在
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name",name).andEqualTo("parentId",parentId);
        List<Category> categoryList = categoryMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(categoryList)){
            flag = false;
        }
        return flag;
    }

    public Integer queryGoodsCount( Long cid, Integer level){
        Example example = new Example(Goods.class);
        Example.Criteria criteria = example.createCriteria();
        if(level == 2){
            criteria.andEqualTo("cid2",cid);
        }else{
            criteria.andEqualTo("cid3",cid);
        }
        return goodsMapper.selectCountByExample(example);
    }

    /**
     * 查询本节点下所包含的所有叶子节点，用于维护tb_category_brand中间表
     * @param category
     * @param leafNode
     */
    private void queryAllLeafNode(Category category,List<Category> leafNode){
        if(!category.getIsParent()){
            leafNode.add(category);
        }
        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("parentId",category.getId());
        List<Category> list=this.categoryMapper.selectByExample(example);

        for (Category category1:list){
            queryAllLeafNode(category1,leafNode);
        }
    }

    /**
     * 查询本节点下所有子节点
     * @param category
     * @param node
     */
    private void queryAllNode(Category category,List<Category> node){

        node.add(category);
        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("parentId",category.getId());
        List<Category> list=this.categoryMapper.selectByExample(example);

        for (Category category1:list){
            queryAllNode(category1,node);
        }
    }
    private List<Category> getCategoryList(List<Category> categoryList) {
        List<Category> list = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        if (!CollectionUtils.isEmpty(categoryList)) {
            for (Category category : categoryList) {
                ids.add(category.getId());
            }

            Example example = new Example(Category.class);
            example.setOrderByClause(" sort ASC");
            example.createCriteria().andIn("parentId", ids).andEqualTo("status",1);
            list = this.categoryMapper.selectByExample(example);
        }
        return list;
    }


    private CategoryBo magerCategoryListAll(Category categoryOne, List<CategoryBo> categoryBoMager) {
        List<CategoryBo> categoryBoList = new ArrayList<>();
        CategoryBo categoryParent = new CategoryBo();
        if (!CollectionUtils.isEmpty(categoryBoMager)) {
            for (CategoryBo categoryBo : categoryBoMager) {
                //判断一级id是否等于二级的父id
                if (categoryOne.getId().longValue() == categoryBo.getParentId().longValue()) {
                    CategoryBo categoryChild = new CategoryBo();
                    categoryChild.setId(categoryBo.getId());
                    categoryChild.setName(categoryBo.getName());
                    categoryChild.setParentId(categoryBo.getParentId());
                    categoryChild.setCategoryList(categoryBo.getCategoryList());
                    categoryBoList.add(categoryBo);
                }
            }
        }
        categoryParent.setId(categoryOne.getId());
        categoryParent.setName(categoryOne.getName());
        categoryParent.setParentId(0L);
        categoryParent.setCategoryList(categoryBoList);
        return categoryParent;
    }

    private List<CategoryBo> magerCategoryList(List<Category> categoryListTwo, List<Category> categoryListThree) {
        List<CategoryBo> categoryBoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(categoryListTwo)) {
            for (Category category : categoryListTwo) {
                CategoryBo categoryBo = addCategoryBo(category, categoryListThree);
                categoryBoList.add(categoryBo);
            }
        }
        return categoryBoList;
    }

    private CategoryBo addCategoryBo(Category category, List<Category> categoryListThree) {
        CategoryBo categoryParent = new CategoryBo();
        List<CategoryBo> categoryBoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(categoryListThree)) {
            for (Category categoryThree : categoryListThree) {
                //二级分类id等于三级分类id
                if (categoryThree.getParentId().longValue() == category.getId().longValue()) {
                    //将三级分类添加到集合中
                    CategoryBo categoryBo = new CategoryBo();
                    categoryBo.setId(categoryThree.getId());
                    categoryBo.setParentId(categoryThree.getParentId());
                    categoryBo.setName(categoryThree.getName());
                    categoryBoList.add(categoryBo);
                }
            }
        }
        categoryParent.setId(category.getId());
        categoryParent.setName(category.getName());
        categoryParent.setParentId(category.getParentId());
        categoryParent.setCategoryList(categoryBoList);
        return categoryParent;
    }


}
