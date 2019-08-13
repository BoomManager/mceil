package com.mceil.item.web;

import com.mceil.common.vo.PageResult;

import com.mceil.item.bo.SpecValueBo;
import com.mceil.item.bo.SpecValueDto;
import com.mceil.item.pojo.SpecValue;
import com.mceil.item.service.SpecValueService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("规格属性值")
@RestController
@RequestMapping("spec/value")
public class SpecValueController {

    @Autowired
    private SpecValueService specValueService;

    @ApiOperation(value = "根据分类id获取规格属性值")
    @GetMapping("/page")
    public ResponseEntity<List<SpecValue>> querySpecValueListByCid(@RequestParam("cid") Long cid){
        List<SpecValue> list = specValueService.querySpecValueListByCid(cid);
        if(list == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(list);
    }
    @ApiOperation(value = "添加规格属性值")
    @PostMapping()
    public ResponseEntity<Void> addSpecCategory(@RequestBody SpecValueDto specValueDto){
        specValueService.addSpecValue(specValueDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ApiOperation(value = "删除规格属性值")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteSpecCategory(@PathVariable("id") Long id,@RequestParam("cid") Long cid){
        specValueService.deleteSpecValue(id,cid);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @ApiOperation(value = "更改规格属性值")
    @PutMapping()
    public ResponseEntity<Void> addSpecCategory(@RequestBody SpecValue specValue){
        specValueService.updateSpecValue(specValue);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
