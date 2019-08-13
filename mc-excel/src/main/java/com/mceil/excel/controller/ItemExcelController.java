package com.mceil.excel.controller;

import com.mceil.excel.pojo.ItemExcel;
import com.mceil.excel.util.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
@Api("Excel接口")
@RestController
@RequestMapping("excel")
public class ItemExcelController {

    @ApiOperation(value = "导入商品表格")
    @PostMapping("/import")
    public ResponseEntity<Void> importExcel2(@RequestParam("file") MultipartFile file) throws IOException {
        List<Object> list = ExcelUtil.readExcel(file, new ItemExcel(),1,1);
        if(!CollectionUtils.isEmpty(list)){
            for (Object o : list) {
                ItemExcel itemExcel = (ItemExcel) o;
                System.out.println("品牌："+itemExcel.getBrandName());
            }
        }
        return ResponseEntity.ok().build();
    }
}
