package com.mceil.item.api;

import com.mceil.item.pojo.Spec;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("spec")
public interface SpecApi {
    @GetMapping("home/{id}")
    ResponseEntity<Spec> querySpecByCid(@PathVariable("id") Long id);

}
