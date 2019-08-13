package com.mceil.user.vo;


import com.mceil.user.pojo.Permission;
import lombok.Data;

import java.util.List;


@Data
public class PermissionNode extends Permission {

    private List<PermissionNode> children;
}
