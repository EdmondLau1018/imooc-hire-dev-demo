package com.imooc.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCompanyBO {

    private String hrUserId;
    private String realname;
    private String hrMobile;
    private String companyId;
    private String authLetter;

//    以下在审核的时候使用到
    private Integer reviewStatus;
    private String reviewReplay;

}
