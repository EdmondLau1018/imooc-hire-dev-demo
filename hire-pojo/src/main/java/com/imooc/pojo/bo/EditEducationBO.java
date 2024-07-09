package com.imooc.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EditEducationBO {

    private String id;
    private String userId;
    private String resumeId;
    private String school;
    private String education;
    private String major;
    private String beginDate;
    private String endDate;

}
