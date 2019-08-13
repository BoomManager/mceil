package com.mceil.item.web;

import com.mceil.common.vo.PageResult;
import com.mceil.item.bo.CategoryBo;
import com.mceil.item.bo.CategoryCount;
import com.mceil.item.pojo.Category;
import com.mceil.item.service.CategoryService;
import com.mceil.item.service.Impl.BrandServiceImpl;
import com.mceil.item.service.Impl.CategoryServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("商品种类接口")
@RestController
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandServiceImpl brandService;

    /**
     * 根據父节点id查詢商品分类
     *
     * @return
     */
    @ApiOperation(value = "分页查询商品分类", notes = "根据父节点id查询商品分类")
    @ApiResponse(code = 200, message = "查询成功，返回相应的种类列表")
    @GetMapping("page/{pid}")
    public ResponseEntity<PageResult<Category>> queryCategoryListByPidPage(@PathVariable("pid") Long pid,@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                       @RequestParam(value = "rows", defaultValue = "5") Integer rows) {
        return ResponseEntity.ok(categoryService.queryCategoryListByPidPage(page,rows,pid));
    }


    @ApiOperation(value = "根据pid查询商品分类", notes = "根据父节点id查询商品分类")
    @ApiResponse(code = 200, message = "查询成功，返回相应的种类列表")
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoryListByPid( @RequestParam("pid") Long pid) {
        return ResponseEntity.ok(categoryService.queryCategoryListByPid(pid));
    }


    /**
     * 根据id查询商品分类
     *
     * @param ids
     * @return
     */
    @ApiOperation(value = "根据ids集合查询商品分类", notes = "根据id集合查询相应的商品种类")
    @ApiImplicitParam(name = "ids", required = true, value = "商品分类id集合")
    @ApiResponse(code = 200, message = "查询成功返回相应的商品分类列表")
    @GetMapping("list/{ids}")
    public ResponseEntity<List<Category>> queryCategoryByIds(@PathVariable("ids") List<Long> ids) {
        return ResponseEntity.ok(categoryService.queryByIds(ids));
    }
    @ApiOperation(value = "根据bid获取分类信息")
    @GetMapping
    public ResponseEntity<List<Category>> queryCategoryBybId(@RequestParam long bid) {
        return ResponseEntity.ok(categoryService.queryCategoryBybId(bid));
    }
/*    @ApiOperation(value = "根据id获取分类信息")
    @GetMapping("/{id}")
    public ResponseEntity<Category> queryCategoryById(@PathVariable Long id){
        Category category = categoryService.queryCategoryById(id);
        if(category == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(category);
    }*/
    //查询3级分类信息（首页展示）
    @ApiOperation(value = "查询3级分类信息（首页展示）")
    @GetMapping("portal")
    public ResponseEntity<List<CategoryBo>> queryCategoryList() {
        List<CategoryBo> list = categoryService.queryCategoryList();
        if (list == null || list.size() < 1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(list);
        }
    }
    /**
     * 保存
     * @return
     */
    @ApiOperation(value = "检验分类名称是否存在")
    @GetMapping("check")
    public ResponseEntity<Boolean> queryCategory(@RequestParam("name") String name,@RequestParam("parentId") Long parentId){
        Boolean flag = this.categoryService.queryCategory(name,parentId);
        return ResponseEntity.ok(flag);
    }
    /**
     * 保存
     * @return
     */
    @ApiOperation(value = "创建分类")
    @PostMapping
    public ResponseEntity<Boolean> saveCategory(@RequestBody Category category){
        Boolean flag = this.categoryService.saveCategory(category);
        return ResponseEntity.ok(flag);
    }

    /**
     * 更新
     * @return
     */
    @ApiOperation(value = "更新分类")
    @PutMapping
    public ResponseEntity<Void> updateCategory(@RequestBody Category category){
        Long parentId = category.getParentId();
        if(parentId == null){
            category.setParentId(0L);
        }
        this.categoryService.updateCategory(category);
        return  ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @ApiOperation(value = "更新分类显示状态")
    @PutMapping("status/{id}")
    public ResponseEntity<Void> updateCategoryStatus(@PathVariable Long id){
        this.categoryService.updateCategoryStatus(id);
        return  ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
    /**
     * 删除
     * @return
     */
    @ApiOperation(value = "删除分类")
    @DeleteMapping("{cid}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("cid") Long cid){
        this.categoryService.deleteCategory(cid);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @ApiOperation(value = "获取二三级分类及其下商品个数")
    @PostMapping("query/count")
    public ResponseEntity<List<CategoryCount>> queryCategoryCount(){
        List<CategoryCount> list = this.categoryService.queryCategoryCountList();
        if (list == null || list.size() < 1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(list);
        }
    }
}
