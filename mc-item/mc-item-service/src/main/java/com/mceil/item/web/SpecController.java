package com.mceil.item.web;

import com.mceil.common.vo.PageResult;
import com.mceil.item.bo.SpecBo;
import com.mceil.item.bo.SpecValueBo;
import com.mceil.item.pojo.Spec;
import com.mceil.item.service.SpecService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("分类规格模板服务接口")
@RestController
@RequestMapping("spec")
public class SpecController {
    @Autowired
    private SpecService specCategoryService;

    @ApiOperation(value = "分页查询分类规格模板", notes = "分页查询分类规格模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = false, value = "分页查询所需要的页数(当前页),默认值为1"),
            @ApiImplicitParam(name = "rows", required = false, value = "一页共有几条数据，默认值为5")
    })
    @ApiResponse(code = 404, message = "分类规格模板不存在")
    @GetMapping("page")
    public ResponseEntity<PageResult<Spec>> querySpecPage(@RequestParam(value = "page" ,defaultValue = "1") Integer page,
                                                            @RequestParam(value = "rows" ,defaultValue = "5") Integer rows){
        PageResult<Spec> result = specCategoryService.queryAllSpec(page,rows);
        if(CollectionUtils.isEmpty(result.getItems())){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "新增分类模板", notes = "根据商品种类(cid1,cid2,ci3)来创建商品品牌")
    @PostMapping()
    public ResponseEntity<Void> saveSpec(@RequestBody Spec spec){
        specCategoryService.saveSpec(spec);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ApiOperation(value = "更新分类模板")
    @PutMapping()
    public ResponseEntity<Void> updateSpec(@RequestBody SpecBo specBo){
        specCategoryService.updateSpec(specBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ApiOperation(value = "删除规格模板")
    @DeleteMapping("/{ids}")
    public ResponseEntity<Void> deleteSpec(@PathVariable("ids") List<Long> ids){
        specCategoryService.deleteSpec(ids);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "获取规格模板")
    @GetMapping("{id}")
    public ResponseEntity<Spec> querySpecById(@PathVariable("id") Long id){
        Spec spec = specCategoryService.querySpecById(id);
        return ResponseEntity.ok(spec);
    }

    @ApiOperation(value = "前台根据分类id获取规格模板")
    @GetMapping("home/{cid}")
    public ResponseEntity<Spec> querySpecByCid(@PathVariable("cid") Long cid){
        Spec spec = specCategoryService.querySpecByCid(cid);
        return ResponseEntity.ok(spec);
    }

    @ApiOperation(value = "前台根据分类id获取规格模板")
    @GetMapping("query/value")
    public ResponseEntity<List<SpecValueBo>> querySpecByValue(@RequestParam("cid") Long cid, @RequestParam(name = "values",required = false) String values){
        List<SpecValueBo> list = specCategoryService.querySpecByValue(cid,values);
        return ResponseEntity.ok(list);
    }
}
