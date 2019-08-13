package com.mceil.user.dto;

import lombok.Data;

@Data
public class MemberResult {
    private Integer isCompany;

    private String personName;

    private String CompanyName;

    private String taxpayerIdentifier;

    private String liaisonMan;

}
