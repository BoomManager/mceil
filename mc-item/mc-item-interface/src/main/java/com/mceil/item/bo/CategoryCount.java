package com.mceil.item.bo;

import lombok.Data;

import java.util.List;
@Data
public class CategoryCount {
    private Long id;
    private Integer count;
    private String name;
    private List<CategoryCount> categoryCounts;

}
