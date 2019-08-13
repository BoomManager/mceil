package com.mceil.excel.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

@Data
public class ItemExcel extends BaseRowModel {
    @ExcelProperty(value = "brandName", index = 0)
    private String brandName;

    @ExcelProperty(value = "cid3", index = 1)
    private String categoryName;

    @ExcelProperty(value = "stock", index = 2)
    private Integer stock;

    @ExcelProperty(value = "prices", index = 3)
    private Integer prices;
}
