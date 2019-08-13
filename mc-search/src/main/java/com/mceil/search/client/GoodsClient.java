package com.mceil.search.client;

import com.mceil.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;


@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {

}
