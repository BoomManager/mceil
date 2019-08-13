package com.mceil.item.api;

import com.mceil.item.bo.BrandBo;
import com.mceil.item.pojo.Brand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequestMapping("brand")
public interface BrandApi {
    @GetMapping("{id}")
    ResponseEntity<BrandBo> queryBrandById(@PathVariable("id") Long id);

/*    @GetMapping("brand/list")
    List<Brand> queryBrandByIds(@RequestParam("ids")List<Long> ids);*/

    //根据二级分类批量插入品牌信息中间表
    @PostMapping("/insert")
    ResponseEntity<Void> batchInsertBrandCategory(@RequestParam("bid") Long bid,@RequestParam("pid") Long pid);
    @GetMapping("list")
    ResponseEntity<List<Brand>> queryBrandByIds(@RequestParam("ids")List<Long> ids);
}
