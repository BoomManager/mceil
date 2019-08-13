package com.mceil.collects.controller;

import com.mceil.collects.pojo.Collects;
import com.mceil.collects.service.CollectsService;
import com.mceil.common.vo.PageResult;
import com.mceil.item.pojo.Category;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Api("收藏接口")
@Controller
@RequestMapping("collects")
public class CollectsController {

    @Autowired
    private CollectsService collectsService;

    @ApiOperation(value = "分页获取收藏列表", notes = "分页获取收藏列表")
    @GetMapping("list")
    public ResponseEntity<PageResult<Collects>> queryCollectsPage(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                                              @RequestParam(value = "rows",defaultValue = "5") Integer rows,
                                                              @RequestParam(value = "key",required = false) String key){
        PageResult<Collects> result = collectsService.queryCollectsPage(page,rows,key);
        if(result == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(result);
    }
/*    @ApiOperation(value = "添加收藏", notes = "添加收藏")
    @PostMapping("add")
    public ResponseEntity<Void> addCollects(@RequestParam("goodsId") Long goodsId){
        collectsService.addCollects(goodsId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }*/
    @ApiOperation(value = "批量添加收藏", notes = "批量添加收藏")
    @ApiImplicitParam(name = "goodsIds", required = true, value = "商品分类id集合")
    @PostMapping("batch/add")
    public ResponseEntity<Void> batchAddCollects(@RequestParam("goodsIds") List<Long> goodsIds){
        collectsService.batchAddCollects(goodsIds);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ApiOperation(value = "是否收藏", notes = "是否收藏")
    @PostMapping("query")
    public ResponseEntity<Boolean> isCollects(@RequestParam("goodsId") Long goodsId){
        Boolean flag = collectsService.isCollects(goodsId);
        return ResponseEntity.ok(flag);
    }
    @ApiOperation(value = "取消收藏", notes = "取消收藏")
    @ApiImplicitParam(name = "ids", required = true, value = "收藏id集合")
    @PostMapping("delete")
    public ResponseEntity<Void> deleteCollects(@RequestParam("ids") List<Long> ids){
        collectsService.deleteCollects(ids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @ApiOperation(value = "添加收藏的备注信息", notes = "添加收藏的备注信息")
    @PostMapping("update")
    public ResponseEntity<Void> updateCollects(@RequestParam("id") Long id,@RequestParam("note") String note){
        collectsService.updateCollects(id,note);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    //获取收藏的三级分类
    @ApiOperation(value = "获取收藏的三级分类", notes = "获取收藏的三级分类")
    @GetMapping("category")
    public ResponseEntity<List<Category>> queryCategoryList(){
        List<Category> categoryList = collectsService.queryCategoryList();

        if(CollectionUtils.isEmpty(categoryList)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(categoryList);
    }
}
