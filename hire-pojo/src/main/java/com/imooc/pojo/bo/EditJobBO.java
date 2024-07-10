package com.imooc.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EditJobBO {

    private String id;
    private String hrId;
    private String companyId;
    private String jobName;
    private String jobType;
    private String expYears;
    private String edu;
    private Integer beginSalary;
    private Integer endSalary;
    private Integer monthlySalary;
    private String jobDesc;
    private String tags;
    private String city;
    private String address;

}
