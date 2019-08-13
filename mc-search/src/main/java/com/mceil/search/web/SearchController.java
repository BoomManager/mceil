package com.mceil.search.web;

import com.mceil.common.vo.PageResult;
import com.mceil.item.bo.GoodsBo;
import com.mceil.item.pojo.Goods;
import com.mceil.search.client.GoodsClient;
import com.mceil.search.pojo.Product;
import com.mceil.search.pojo.SearchRequest;
import com.mceil.search.repository.GoodsRespository;
import com.mceil.search.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Api("搜索接口")
@RestController
public class SearchController implements InitializingBean{
    @Autowired
    private SearchService searchService;
    @Autowired
    private GoodsRespository goodsRespository;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @ApiOperation(value = "从ElasticSearch的桶查询商品信息")
    @PostMapping("page")
    public ResponseEntity<PageResult<Product>> search(@RequestBody SearchRequest request){
        System.out.println(request);
        return ResponseEntity.ok(searchService.search(request));
    }
    @GetMapping("all")
    public ResponseEntity<Iterable<Product>> getAll(){
        return ResponseEntity.ok(searchService.getAll());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 创建索引
        this.elasticsearchTemplate.createIndex(Product.class);
        // 配置映射
        this.elasticsearchTemplate.putMapping(Product.class);
        //加载数据
        List<Goods> list = new ArrayList<>();
        int page = 1;
        int row = 100;
        int size;
        do {
            //分页查询数据
            PageResult<Goods> result = this.goodsClient.queryGoodsByPage(page,row,true,null,null).getBody();
            List<Goods> goodsList = result.getItems();
            size = goodsList.size();
            page ++;
            list.addAll(goodsList);
        }while (size == 100);
        //创建一个product集合
        List<Product> productList = new ArrayList<>();
        for (Goods goods : list) {
            try {
                Product product = this.searchService.buildGoods(goods);
                productList.add(product);
            }catch (Exception e){
                System.out.println("查询失败：" + goods.getId());
            }
        }
        goodsRespository.saveAll(productList);
        System.out.println("导入索引库成功");
    }
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteAll(){
        searchService.deleteAll();
        return ResponseEntity.ok().build();
    }
}
