package com.imooc.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SearchResumesVO {

    private String userId;
    private String resumeId;
    private String nickname;
    private Integer sex;
    private String face;
    private LocalDate birthday;
    private Integer age;

    private String companyName;
    private String position;
    private String industry;

    private String school;
    private String education;
    private String major;

    private String resumeExpectId;
    private Integer workYears;
    private String jobType;
    private String city;

    private Integer beginSalary;
    private Integer endSalary;

    private String skills;
    private String advantage;
    private String advantageHtml;
    private String credentials;
    private String jobStatus;
    private LocalDateTime refreshTime;

    // HR收藏简历的时间
    private LocalDateTime hrCollectResumeTime;
    // HR浏览简历的时间
    private LocalDateTime hrReadResumeTime;

}
