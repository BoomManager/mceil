package com.mceil.search.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mceil.common.enums.ExceptionEnum;
import com.mceil.common.exception.McException;

import com.mceil.common.utils.JsonUtils;
import com.mceil.common.utils.NumberUtils;
import com.mceil.item.bo.PriceBo;
import com.mceil.item.pojo.*;
import com.mceil.search.client.BrandClient;
import com.mceil.search.client.CategoryClient;
import com.mceil.search.client.GoodsClient;
import com.mceil.search.client.SpecClient;
import com.mceil.search.pojo.Product;
import com.mceil.search.pojo.SearchRequest;
import com.mceil.search.pojo.SearchResult;
import com.mceil.search.repository.GoodsRespository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchService {
    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private SpecClient specClient;

    @Autowired
    private GoodsRespository goodsRespository;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private ElasticsearchTemplate template;

    public Product buildGoods(Goods goods) throws IOException {
        //查询分类

        List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(goods.getCid1(), goods.getCid2(), goods.getCid3())).getBody();
        List<String> categoryName = categories.stream().map(Category::getName).collect(Collectors.toList());
        //查询品牌
        Brand brand = brandClient.queryBrandById(goods.getBrandId()).getBody();
        //搜索字段
        String all=goods.getTitle() + " " + StringUtils.join(categoryName," ") + " "+ brand.getName() + " "+ goods.getGoodsModel() + " "+ goods.getGoodsSn();

        Stock stock = goodsClient.queryStockById(goods.getId()).getBody();

        //构建goods对象
        Product product = new Product();
        String prices = goods.getPrices();
        List<Long> pricesList = new ArrayList<>();
        List<PriceBo> priceBos = JsonUtils.toList(prices,PriceBo.class);
        List<Map<String,Object>> priceList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(priceBos)){
            Map<String,Object> priceMap = new HashMap<>();
            for (PriceBo priceBo : priceBos) {
                priceMap.put("count",priceBo.getCount());
                priceMap.put("price",priceBo.getPrice());
                priceList.add(priceMap);
                pricesList.add(priceBo.getPrice());
            }
        }
        product.setSale(goods.getSale());
        product.setTitle(goods.getTitle());
        product.setBrandId(goods.getBrandId());
        product.setCid1(goods.getCid1());
        product.setCid2(goods.getCid2());
        product.setCid3(goods.getCid3());
        product.setCreateTime(goods.getCreateTime());
        product.setId(goods.getId());
        product.setBname(brand.getName());
        product.setGoodsModel(goods.getGoodsModel());
        product.setProductSn(goods.getGoodsSn());
        product.setStock(stock.getStock());
        product.setPrices(pricesList);

        if(goods.getImages().contains(",")){
            //获取第一张图片地址
            product.setImages(goods.getImages().split(",")[0]);
        }else{
            product.setImages(goods.getImages());
        }
        String ownSpec = goods.getOwnSpec();
        //1.将规格反序列化为集合
        List<Map<String,Object>> specs = null;
        specs = JsonUtils.nativeRead(ownSpec, new TypeReference<List<Map<String, Object>>>() {
        });
       Map<String, Object> specResult = new HashMap<>();
       //构建商品特有属性
       specs.forEach( param ->{
            String key = (String) param.get("name");
            List<String> list = (List<String>) param.get("dynamicTags");
            specResult.put(key.toString(),list);
        });
        product.setSpecs(specResult);
        product.setAll(all);// 搜索字段，包含标题，分类，品牌，规格等

        product.setSubTitle(goods.getSubTitle());
        product.setPrice(mapper.writeValueAsString(priceList));
        return product;
    }
    public void createOrUpdateIndex(Long goodsId) throws IOException{
        //查询spu
        Goods goods = goodsClient.queryGoodsById(goodsId).getBody();
        //构建goods
        Product product = buildGoods(goods);
        //存入索引库
        goodsRespository.save(product);

        System.out.println("导入索引库成功！");
    }

    //删除索引
    public void deleteIndex(Long spuId) {
        goodsRespository.deleteById(spuId);
    }


    public SearchResult<Product> search(SearchRequest searchRequest){
        String key = searchRequest.getKey();

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //通过sourceFilter字段过滤只要我们需要的数据
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "title","subTitle","brandId","bname","images","productSn","goodsModel","price","sale","stock","specs"}, null));
        //分页和排序
        searchWithPageAndSort(queryBuilder, searchRequest);
        if (StringUtils.isBlank(key)) {
            //throw new McException(ExceptionEnum.INVALID_PARAM);
            return null;
        }
        //基本搜索条件

        QueryBuilder basicQuery = buildBasicQuery(searchRequest);
        queryBuilder.withQuery(basicQuery);
        //对分类和品牌聚合
        String categoryAggName = "categoryAgg";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));

        String brandAggName = "brandAgg";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        //查询，获取结果
        AggregatedPage<Product> result = template.queryForPage(queryBuilder.build(), Product.class);

        //解析聚合结果
        Aggregations aggs = result.getAggregations();

        //解析分类聚合
        List<Category> categories = handleCategoryAgg(aggs.get(categoryAggName));
        List<Map<String, Object>> specs = null;
       if (!CollectionUtils.isEmpty(categories)){
           //判断分类数据是否为空
           if(categories.size() == 1 ){
               specs = getSpec(categories.get(0).getId(),basicQuery);
           }
       }


        //解析品牌聚合
        List<Brand> brands = handleBrandAgg(aggs.get(brandAggName));
        //对规格参数聚合

        //解析分页结果
        long total = result.getTotalElements();
        int totalPage = result.getTotalPages();
        List<Product> items = result.getContent();

        return new SearchResult(total, totalPage, items, categories, brands, specs);
    }

    private List<Map<String, Object>> getSpec(Long id, QueryBuilder basicQuery) {
        String specJsonStr = specClient.querySpecByCid(id).getBody().getSpecTemplate();
        //1.将规格反序列化为集合
        List<Map<String,Object>> specs = null;
        //2.过滤出可以搜索的规格参数名称，分成数值类型、字符串类型
        Set<String> strSpec = new HashSet<>();
        specs = JsonUtils.nativeRead(specJsonStr, new TypeReference<List<Map<String, Object>>>() {
        });
        specs.forEach( param ->{
            String key = (String) param.get("name");
            strSpec.add(key);
        });
        return this.aggForSpec(strSpec,basicQuery);
    }

    private List<Map<String, Object>> aggForSpec(Set<String> strSpec, QueryBuilder basicQuery) {
        List<Map<String,Object>> specs = new ArrayList<>();
        //准备查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(basicQuery);
        //聚合字符串
        for (String key :strSpec){
            queryBuilder.addAggregation(AggregationBuilders.terms(key).field("specs."+key+".keyword"));
        }
        //解析聚合结果
        Map<String,Aggregation> aggregationMap = this.template.query(queryBuilder.build(), SearchResponse :: getAggregations).asMap();
        //解析字符串类型
        strSpec.forEach(key -> {
            Map<String,Object> spec = new HashMap<>();
            spec.put("k",key);
            StringTerms terms = (StringTerms) aggregationMap.get(key);
            spec.put("options",terms.getBuckets().stream().map((Function<StringTerms.Bucket, Object>) StringTerms.Bucket::getKeyAsString).collect(Collectors.toList()));
            specs.add(spec);
        });
        return specs;
    }

    /**
     * 分页和排序
     *
     * @param queryBuilder
     * @param searchRequest
     */
    private void searchWithPageAndSort(NativeSearchQueryBuilder queryBuilder, SearchRequest searchRequest) {
        Integer page = searchRequest.getPage() - 1;
        Integer size = searchRequest.getSize();

        String sortBy = searchRequest.getSortBy();
        Boolean desc = searchRequest.getDescending();

        //分页
        queryBuilder.withPageable(PageRequest.of(page, size));

        //排序
        if (StringUtils.isNotBlank(sortBy)) {
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(desc ? SortOrder.DESC : SortOrder.ASC));
        }
    }
    /**
     * 构建基本查询
     *
     * @param request
     * @return
     */
    private QueryBuilder buildBasicQuery(SearchRequest request) {
        //构建布尔查询
        BoolQueryBuilder basicQuery = QueryBuilders.boolQuery();
        //搜索条件
        basicQuery.must(QueryBuilders.matchQuery("all", request.getKey()));
        //过滤条件构造器
        BoolQueryBuilder filterQueryBuilder = QueryBuilders.boolQuery();

        //过滤条件
        Map<String, String> filter = request.getFilter();

        for (Map.Entry<String,String> entry : filter.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String regex = "^(\\d+\\.?\\d*)-(\\d+\\.?\\d*)$";
            if (!"key".equals(key)) {
                if ("price".equals(key)){
                    if (!value.contains("元以上")) {
                        String[] nums = StringUtils.substringBefore(value, "元").split("-");
                        filterQueryBuilder.must(QueryBuilders.rangeQuery(key).gte(Double.valueOf(nums[0])).lt(Double.valueOf(nums[1])));
                    }else {
                        String num = StringUtils.substringBefore(value,"元以上");
                        filterQueryBuilder.must(QueryBuilders.rangeQuery(key).gte(Double.valueOf(num)));
                    }
                }else {
                    if (value.matches(regex)) {
                        Double[] nums = NumberUtils.searchNumber(value, regex);
                        //数值类型进行范围查询   lt:小于  gte:大于等于
                        filterQueryBuilder.must(QueryBuilders.rangeQuery("specs." + key).gte(nums[0]).lt(nums[1]));
                    } else {
                        //商品分类和品牌要特殊处理
                        if (key != "cid3" && key != "brandId") {
                            key = "specs." + key + ".keyword";
                        }
                        //字符串类型，进行term查询
                        filterQueryBuilder.must(QueryBuilders.termQuery(key, value));
                    }
                }
            } else {
                break;
            }
        }
        //添加过滤条件
        basicQuery.filter(filterQueryBuilder);
        return basicQuery;
    }
    /**
     * 对分类聚合结果进行解析
     *
     * @param terms
     * @return
     */
    public List<Category> handleCategoryAgg(LongTerms terms) {
        try {
            //获取聚合桶中的cid3集合
            List<Long> ids = terms.getBuckets()
                    .stream()
                    .map(b -> b.getKeyAsNumber().longValue())
                    .collect(Collectors.toList());
            //根据ID查询分类
            if(!CollectionUtils.isEmpty(ids)){
                List<Category> categories = categoryClient.queryCategoryByIds(ids).getBody();
                for (Category category : categories) {
                    category.setParentId(null);
                    category.setIsParent(null);
                    category.setSort(null);
                }
                return categories;
            }
            return null;
        } catch (Exception e) {
            log.error("查询分类信息失败", e);
            return null;
        }
    }
        /**
         * 解析品牌聚合结果
         *
         * @param terms
         * @return
         */
        private List<Brand> handleBrandAgg(LongTerms terms) {
            //获取品牌ID
            try {
                List<Long> ids = terms.getBuckets()
                        .stream()
                        .map(b -> b.getKeyAsNumber().longValue())
                        .collect(Collectors.toList());
                //根据品牌ids查询品牌

                if(!CollectionUtils.isEmpty(ids)){
                    List<Brand> brands =  brandClient.queryBrandByIds(ids).getBody();
                    return brands;
                }
                return null;

            } catch (Exception e) {
                log.error("查询品牌信息失败", e);
                return null;
            }
        }


    /**
     * 解析规格聚合结果
     *
     * @param terms
     * @return
     */
    private List<Brand> handleSpecAgg(LongTerms terms) {
        //获取品牌ID
        try {
            List<Long> ids = terms.getBuckets()
                    .stream()
                    .map(b -> b.getKeyAsNumber().longValue())
                    .collect(Collectors.toList());
            //根据品牌ids查询品牌
            List<Brand> brands =  brandClient.queryBrandByIds(ids).getBody();
            return brands;

        } catch (Exception e) {
            log.error("查询品牌信息失败", e);
            return null;
        }
    }

    public Iterable<Product> getAll() {
        Iterable<Product> iterator = goodsRespository.findAll();
        return iterator;
    }
    public void deleteAll(){
        goodsRespository.deleteAll();
    }
}
