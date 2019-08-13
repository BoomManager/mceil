package com.mceil.item.bo;

import lombok.Data;

import java.util.List;
@Data
public class SpecValueBo {
    private String name;
    private List<SpecStatus> specStatusList;
}
