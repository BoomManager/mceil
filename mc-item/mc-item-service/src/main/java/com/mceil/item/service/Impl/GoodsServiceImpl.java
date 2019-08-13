package com.mceil.item.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mceil.common.dto.CartDTO;
import com.mceil.common.enums.ExceptionEnum;
import com.mceil.common.exception.McException;
import com.mceil.common.vo.PageResult;
import com.mceil.item.bo.GoodsBo;
import com.mceil.item.bo.GoodsSnCreate;
import com.mceil.item.bo.PriceBo;
import com.mceil.item.mapper.*;
import com.mceil.item.pojo.*;
import com.mceil.item.service.CategoryService;
import com.mceil.item.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private GoodsDetailMapper goodsDetailMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Override
    public PageResult<Goods>  queryGoodsByPage(Integer page, Integer rows, Boolean saleable, String keyword,String goodsSn) {
        PageHelper.startPage(page,rows);
        //创建查询条件
        Example example = new Example(Goods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDelete",1);
        //条件过滤
        //是否过滤上下架
        if(saleable != null){
            criteria.andEqualTo("saleable",saleable);
        }

        //是否模糊查询
        if(StringUtils.isNotBlank(keyword)){
            criteria.andLike("title","%"+keyword+"%");
        }
        if(StringUtils.isNotBlank(goodsSn)){
            criteria.andLike("goodsSn",goodsSn);
        }
        List<Goods> list = goodsMapper.selectByExample(example);
        saveBname(list);
        saveCname(list);

        //int count = goodsMapper.selectCountByExample(example);
        //添加库存
        //saveStock(list);
 /*       //将Goods变为GoodsBo
        List<GoodsBo> list = goodsList.stream().map(goods ->{
            GoodsBo goodsBo = new GoodsBo();
            //1.属性拷贝
            BeanUtils.copyProperties(goods,goodsBo);
            //2.查询spu的商品分类名称，各级分类
            List<String> nameList = this.categoryService.queryNameByIds(Arrays.asList(goods.getCid1(),goods.getCid2(),goods.getCid3()));
            //3.拼接名字,并存入
            goodsBo.setCname(org.apache.commons.lang3.StringUtils.join(nameList,"/"));
            Brand brand = this.brandMapper.selectByPrimaryKey(goods.getBrandId());
            goodsBo.setBname(brand.getName());
            //查询品牌名称
            return goodsBo;
        }).collect(Collectors.toList());*/

        //解析分页结果
        PageInfo<Goods> info = new PageInfo<>(list);

        return new PageResult<>(info.getTotal(), list);
    }

    private void saveCname(List<Goods> list) {
        if(!CollectionUtils.isEmpty(list)){
            for (Goods goods : list) {
                //根据分类id集合获取分类信息
                List<Category> categories = categoryService.queryByIds(Arrays.asList(goods.getCid1(), goods.getCid2(), goods.getCid3()));
                //获取分类名称信息集合
                List<String> categoryName = categories.stream().map(Category::getName).collect(Collectors.toList());
                //集合转字符串，以“，”隔开
                goods.setCname(org.apache.commons.lang3.StringUtils.join(categoryName,","));
            }
        }
    }

    private void saveBname(List<Goods> list) {
        if(!CollectionUtils.isEmpty(list)){
            for (Goods goods : list) {
                Brand brand = brandMapper.selectByPrimaryKey(goods.getBrandId());
                if(brand != null){
                    goods.setBname(brand.getName());
                }

            }
        }
    }

    private void saveStock(List<Goods> goodsList) {
        if(!CollectionUtils.isEmpty(goodsList)){
            for (Goods goods : goodsList) {
                Stock stock = stockMapper.selectByPrimaryKey(goods.getId());
                goods.setStock(stock);
            }
        }
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveGoods(GoodsBo goodsBo) {
        //保存goods
        //goodsBo.setSaleable(true);
        goodsBo.setCreateTime(new Date());
        goodsBo.setLastUpdateTime(goodsBo.getCreateTime());
        goodsBo.setGoodsSn("C" + GoodsSnCreate.getNumber());
        goodsBo.setIsDelete(true);
        goodsBo.setCheckStatus(0);
        this.goodsMapper.insert(goodsBo);

        //保存goods详情
        GoodsDetail goodsDetail = goodsBo.getGoodsDetail();
        goodsDetail.setGoodsId(goodsBo.getId());
        this.goodsDetailMapper.insertSelective(goodsDetail);

        //保存商品和库存
        Stock stock = goodsBo.getStock();
        stock.setGoodsId(goodsBo.getId());
        this.stockMapper.insertSelective(stock);
        sendMessage(goodsBo.getId(),"insert");
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateGoods(GoodsBo goodsBo) {
        //保存goods
        goodsBo.setSaleable(true);
        goodsBo.setLastUpdateTime(new Date());
        this.goodsMapper.updateByPrimaryKeySelective(goodsBo);

        //保存goods详情
        GoodsDetail goodsDetail = goodsBo.getGoodsDetail();
        goodsDetail.setGoodsId(goodsBo.getId());
        this.goodsDetailMapper.updateByPrimaryKeySelective(goodsDetail);

        //保存商品和库存
        Stock stock = goodsBo.getStock();
        stock.setGoodsId(goodsBo.getId());
        this.stockMapper.updateByPrimaryKeySelective(stock);
        sendMessage(goodsBo.getId(),"update");
    }

    @Override
    public GoodsBo queryGoodsById(Long id) {
        GoodsBo goodsBo = new GoodsBo();
        Goods goods = goodsMapper.selectByPrimaryKey(id);
        GoodsDetail goodsDetail = goodsDetailMapper.selectByPrimaryKey(id);
        Stock stock = stockMapper.selectByPrimaryKey(id);
        goods.setStock(stock);
        //属性拷贝
        BeanUtils.copyProperties(goods,goodsBo);
        //2.查询spu的商品分类名称，各级分类
        List<String> nameList = this.categoryService.queryNameByIds(Arrays.asList(goods.getCid1(),goods.getCid2(),goods.getCid3()));
        //3.拼接名字,并存入
        goodsBo.setCname(org.apache.commons.lang3.StringUtils.join(nameList,"/"));
        Brand brand = this.brandMapper.selectByPrimaryKey(goods.getBrandId());
        goodsBo.setBname(brand.getName());
        goodsBo.setGoodsDetail(goodsDetail);
        return goodsBo;
    }

    @Override
    public GoodsDetail queryGoodsDetailById(Long id) {
        return goodsDetailMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGoodsNewStatusBatch(List<Long> ids,int status) {
        for (Long id : ids) {
            Goods goods = goodsMapper.selectByPrimaryKey(id);
            if(status == 1){
                goods.setNewStatus(true);
            }else{
                goods.setNewStatus(false);
            }
            goodsMapper.updateByPrimaryKeySelective(goods);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGoodsRecommendBatch(List<Long> ids,int status) {
        for (Long id : ids) {
            Goods goods = goodsMapper.selectByPrimaryKey(id);
            if(status == 1){
                goods.setRecommandStatus(true);
            }else{
                goods.setRecommandStatus(false);
            }
            goodsMapper.updateByPrimaryKeySelective(goods);
        }

    }

    @Override
    @Transactional
    public void GoodsSoldOut(Long id) {
        Goods goods = goodsMapper.selectByPrimaryKey(id);
        if(goods.getSaleable()){
            goods.setSaleable(false);
        }else{
            goods.setSaleable(true);
        }
        goodsMapper.updateByPrimaryKeySelective(goods);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteGoods(List<Long> ids) {
        //删除商品表
        List<Goods> list = goodsMapper.selectByIdList(ids);
        if(!CollectionUtils.isEmpty(list)){
            for (Goods goods : list) {
                goods.setIsDelete(false);
                goodsMapper.updateByPrimaryKeySelective(goods);
                //发送消息队列
                sendMessage(goods.getId(),"delete");
            }
        }


/*        //删除库存表
        stockMapper.deleteByIdList(ids);
        //删除详情表
        goodsDetailMapper.deleteByIdList(ids);*/
    }

    @Override
    public PageResult<Goods> queryGoodsNewStatus(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        Example example = new Example(Goods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("newStatus",1).andEqualTo("saleable",1);
        List<Goods> list = goodsMapper.selectByExample(example);
        //解析分页结果
        PageInfo<Goods> info = new PageInfo<>(list);

        return new PageResult<>(info.getTotal(), list);
    }

    @Override
    public PageResult<Goods> queryGoodsRecommend(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        Example example = new Example(Goods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("recommandStatus",1).andEqualTo("saleable",1);
        List<Goods> list = goodsMapper.selectByExample(example);
        //解析分页结果
        PageInfo<Goods> info = new PageInfo<>(list);

        return new PageResult<>(info.getTotal(), list);
    }

    @Override
    @Transactional
    public void updateGoodsNewStatus(Long id) {
        Goods goods = goodsMapper.selectByPrimaryKey(id);
        if(goods.getNewStatus()){
            goods.setNewStatus(false);
        }else{
            goods.setNewStatus(true);
        }
        goodsMapper.updateByPrimaryKeySelective(goods);
    }

    @Override
    @Transactional
    public void updateGoodsRecommend(Long id) {
        Goods goods = goodsMapper.selectByPrimaryKey(id);
        if(goods.getRecommandStatus()){
            goods.setRecommandStatus(false);
        }else{
            goods.setRecommandStatus(true);
        }
        goodsMapper.updateByPrimaryKeySelective(goods);
    }

    @Override
    @Transactional
    public void updateCheckStatus(Long id, int status) {
        Goods goods = goodsMapper.selectByPrimaryKey(id);
        if(goods != null){
            goods.setCheckStatus(status);
        }
        goodsMapper.updateByPrimaryKeySelective(goods);
    }

    @Override
    public List<Goods> queryGoodsByIds(List<Long> ids) {
        Example example = new Example(Goods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",ids);
        List<Goods> list = goodsMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(list)){
            list.forEach(goods -> {
                //2.查询spu的商品分类名称，各级分类
                List<String> nameList = this.categoryService.queryNameByIds(Arrays.asList(goods.getCid1(),goods.getCid2(),goods.getCid3()));
                //3.拼接名字,并存入
                goods.setCname(org.apache.commons.lang3.StringUtils.join(nameList,"/"));
                Brand brand = this.brandMapper.selectByPrimaryKey(goods.getBrandId());
                goods.setBname(brand.getName());
                Stock stock = stockMapper.selectByPrimaryKey(goods.getId());
                goods.setStock(stock);
            });
        }
        return list;
    }

    @Override
    @Transactional
    public void decreaseStock(List<CartDTO> carts) {
        if(!CollectionUtils.isEmpty(carts)){
            for (CartDTO cart : carts) {
                Stock stock = stockMapper.selectByPrimaryKey(cart.getGoodsId());
                if(stock.getStock() > cart.getNum()){
                    stock.setStock(stock.getStock() - cart.getNum());
                    stockMapper.updateByPrimaryKeySelective(stock);
                }
                throw new McException(ExceptionEnum.STOCK_NOT_ENOUGH);
            }

        }
    }

    @Override
    public PageResult<Goods> queryGoodsByPageIndex(Integer page, Integer rows, String key, Long cid) {
        PageHelper.startPage(page,rows);
        //创建查询条件
        Example example = new Example(Goods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDelete",1).andEqualTo("saleable",1)
                .andEqualTo("checkStatus",1).orEqualTo("cid2",cid).orEqualTo("cid3",cid);

        //是否模糊查询
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        List<Goods> list = goodsMapper.selectByExample(example);
        saveBname(list);
        //解析分页结果
        PageInfo<Goods> info = new PageInfo<>(list);

        return new PageResult<>(info.getTotal(), list);
    }

    @Override
    public Stock queryStockById(Long id) {

        return stockMapper.selectByPrimaryKey(id);
    }

    /**
     * 封装发送到消息队列的方法
     *
     * @param id
     * @param type
     */
    private void sendMessage(Long id, String type) {
        try {
            amqpTemplate.convertAndSend("item." + type, id);
        } catch (Exception e) {
            log.error("{}商品消息发送异常，商品ID：{}", type, id, e);
        }
    }
}
