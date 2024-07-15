package com.imooc.pojo.eo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(indexName = "resume_result")
public class SearchResumesEO {

    @Field
    private String userId;
    @Field
    private String resumeId;
    @Field
    private String nickname;
    @Field
    private Integer sex;
    @Field
    private String face;
    @Field
    private String birthday;
    @Field
    private Integer age;

    @Field
    private String companyName;
    @Field
    private String position;
    @Field
    private String industry;

    @Field
    private String school;
    @Field
    private String education;
    @Field
    private String major;

    @Id
    private String resumeExpectId;
    @Field
    private Integer workYears;
    @Field
    private String jobType;
    @Field
    private String city;

    @Field
    private Integer beginSalary;
    @Field
    private Integer endSalary;

    @Field
    private String skills;
    @Field
    private String advantage;
    @Field
    private String advantageHtml;
    @Field
    private String credentials;
    @Field
    private String jobStatus;
    @Field
    private String refreshTime;

    // HR收藏简历的时间
    @Field
    private String hrCollectResumeTime;
    // HR浏览简历的时间
    @Field
    private String hrReadResumeTime;

}
