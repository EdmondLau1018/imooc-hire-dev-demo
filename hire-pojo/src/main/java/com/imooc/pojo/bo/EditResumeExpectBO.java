package com.imooc.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EditResumeExpectBO {

    private String id;
    private String userId;
    private String resumeId;
    private String jobName;
    private String city;
    private String industry;
    private Integer beginSalary;
    private Integer endSalary;
}
