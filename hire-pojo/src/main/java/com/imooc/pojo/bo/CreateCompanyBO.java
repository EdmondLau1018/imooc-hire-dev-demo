package com.imooc.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateCompanyBO implements Serializable {

    private String companyId;
    private String companyName;
    private String shortName;
    private String logo;
    private String bizLicense;
    private String peopleSize;
    private String industry;

    /**
     * 审核状态
     * 0 未发起审核
     * 1 审核认证通过
     * 2 审核认证失败
     * 3 审核中
     */
    private Integer reviewStatus;

}
