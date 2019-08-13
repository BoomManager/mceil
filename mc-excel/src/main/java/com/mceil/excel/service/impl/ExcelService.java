package com.mceil.excel.service.impl;

import com.mceil.excel.client.BrandClient;
import com.mceil.excel.client.CategoryClient;
import com.mceil.excel.client.GoodsClient;
import com.mceil.excel.client.SpecClient;
import com.mceil.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExcelService {
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private SpecClient specClient;


}
