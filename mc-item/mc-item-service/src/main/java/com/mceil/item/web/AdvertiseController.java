package com.mceil.item.web;

import com.mceil.common.vo.PageResult;
import com.mceil.item.pojo.Advertise;
import com.mceil.item.service.AdvertiseService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Api("轮播图接口")
@Controller
@RequestMapping("advertise")
public class AdvertiseController {
    @Autowired
    private AdvertiseService advertiseService;

    @ApiOperation(value = "分页查询轮播图", notes = "分页查询轮播图,返回分页后的轮播图列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = false, value = "分页查询所需要的页数(当前页),默认值为1"),
            @ApiImplicitParam(name = "rows", required = false, value = "一页共有几条数据，默认值为5"),
            @ApiImplicitParam(name = "name", required = false, value = "轮播图名称"),
            @ApiImplicitParam(name = "endTime", required = false, value = "到期时间")
    })
    @ApiResponse(code = 404, message = "广告不存在")
    @GetMapping("page")
    public ResponseEntity<PageResult<Advertise>> queryAdvertisePage(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                                                    @RequestParam(value = "rows",defaultValue = "5") Integer rows,
                                                                    @RequestParam(value = "name",required = false) String name,
                                                                    @RequestParam(value = "endTime",required = false) Date endTime){

        PageResult<Advertise> result = advertiseService.queryAdvertisePage(page,rows,name,endTime);
        if(!CollectionUtils.isEmpty(result.getItems())){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "新增轮播图", notes = "新增轮播图")
    @PostMapping()
    public ResponseEntity<Void> saveAdvertise(@RequestBody Advertise advertise){
        advertiseService.saveAdvertise(advertise);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ApiOperation(value = "更新轮播图", notes = "更新轮播图")
    @PutMapping()
    public ResponseEntity<Void> updateAdvertise(@RequestBody Advertise advertise){
        advertiseService.updateAdvertise(advertise);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ApiOperation(value = "删除轮播图", notes = "删除轮播图")
    @DeleteMapping("{ids}")
    public ResponseEntity<Void> deleteAdvertise(@PathVariable("ids")List<Long> ids){
        advertiseService.deleteAdvertise(ids);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "根据id获取轮播图内容", notes = "根据id获取轮播图内容")
    @GetMapping("{ids}")
    public ResponseEntity<Advertise> queryAdvertiseById(@PathVariable("id")Long id){
        Advertise advertise =  advertiseService.queryAdvertiseById(id);
        if(advertise == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(advertise);
    }

    @ApiOperation(value = "分页查询轮播图,提供给前台的接口", notes = "分页查询轮播图,返回分页后的广告列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = false, value = "分页查询所需要的页数(当前页),默认值为1"),
            @ApiImplicitParam(name = "rows", required = false, value = "一页共有几条数据，默认值为5"),
            @ApiImplicitParam(name = "name", required = false, value = "广告名称"),
            @ApiImplicitParam(name = "endTime", required = false, value = "到期时间")
    })

    @ApiResponse(code = 404, message = "广告不存在")
    @GetMapping("home")
    public ResponseEntity<PageResult<Advertise>> queryAdvertisePagePortal(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                                                    @RequestParam(value = "rows",defaultValue = "5") Integer rows){

        PageResult<Advertise> result = advertiseService.queryAdvertisePagePortal(page,rows);
        if(CollectionUtils.isEmpty(result.getItems())){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }
}
