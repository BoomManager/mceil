package com.mceil.item.bo;

import com.mceil.item.pojo.SpecValue;
import lombok.Data;

import java.util.List;
@Data
public class SpecValueDto {
    private Long cid;
    private List<SpecValue> specValueList;
}
