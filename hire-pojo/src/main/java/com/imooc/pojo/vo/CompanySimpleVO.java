package com.imooc.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CompanySimpleVO {

    private String id;
    private String companyName;
    private String shortName;
    private String logo;

    private String peopleSize;
    private String industry;
    private String nature;
    private String address;

    /**
     * 审核状态
     0：未发起审核认证(未进入审核流程)
     1：审核认证通过
     2：审核认证失败
     3：审核中（等待审核）
     */
    private Integer reviewStatus;
    private String reviewReplay;

    // 企业下所绑定的HR数量
    private Long hrCounts = Long.valueOf("0");
}
