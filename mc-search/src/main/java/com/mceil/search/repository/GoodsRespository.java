package com.mceil.search.repository;

import com.mceil.search.pojo.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GoodsRespository extends ElasticsearchRepository<Product,Long> {
}
