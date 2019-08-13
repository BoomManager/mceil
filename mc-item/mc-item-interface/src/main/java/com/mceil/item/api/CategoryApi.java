package com.mceil.item.api;

import com.mceil.item.pojo.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@RequestMapping("category")
public interface CategoryApi {
    @GetMapping("list/{ids}")
    ResponseEntity<List<Category>> queryCategoryByIds(@PathVariable("ids") List<Long> ids);
}
