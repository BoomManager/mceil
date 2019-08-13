package com.mceil.item.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryBo  {
    private Long id;
    private Long parentId;
    private String name;
    private List<CategoryBo> categoryList;
}
