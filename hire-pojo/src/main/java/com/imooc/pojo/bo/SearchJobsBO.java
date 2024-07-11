package com.imooc.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SearchJobsBO {

    private String jobName;
    private String jobType;
    private String city;
    private Integer beginSalary;
    private Integer endSalary;

}
