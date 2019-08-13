package com.mceil.item.web;

import com.mceil.common.vo.PageResult;
import com.mceil.item.pojo.SpecCategory;
import com.mceil.item.service.SpecCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api("规格分类")
@RestController
@RequestMapping("spec/category")
public class SpecCategoryController {
    @Autowired
    private SpecCategoryService specCategoryService;

    @ApiOperation(value = "分页获取规格分类")
    @GetMapping("/page")
    public ResponseEntity<PageResult<SpecCategory>> querySpecCategoryPage(@RequestParam("page") Integer page,
                                                                      @RequestParam("rows") Integer rows){
        PageResult<SpecCategory> result = specCategoryService.querySpecCategoryPage(page,rows);
        if(result == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "添加规格分类")
    @PostMapping()
    public ResponseEntity<Void> addSpecCategory(@RequestParam("cid") Long cid){
        specCategoryService.addSpecCategory(cid);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ApiOperation(value = "删除规格分类")
    @DeleteMapping("/delete/{cid}")
    public ResponseEntity<Void> deleteSpecCategory(@RequestParam("cid") Long cid){
        specCategoryService.deleteSpecCategory(cid);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
