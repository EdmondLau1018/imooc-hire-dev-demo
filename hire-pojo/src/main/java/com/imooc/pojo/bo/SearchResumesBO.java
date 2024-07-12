package com.imooc.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SearchResumesBO {

    private String companyId;

    private String basicTitle;      // 该字段可以用于模糊搜索(匹配)简历的 姓名、个人优势、资格证书、技能标签
    private String jobType;

    private Integer beginAge;
    private Integer endAge;
    private Integer sex;

    // 活跃度
    private String activeTime;
    private Integer activeTimes;    // 后端根据前端传来的[activeTime字符串]从枚举中计算获得的[活跃度时间(秒)]

    // 工作经验
    private String workExpYears;
    private Integer beginWorkExpYears;
    private Integer endWorkExpYears;

    // 学历要求
    private String edu;
    private List<String> eduList;

    // 薪资待遇
    private Integer beginSalary;
    private Integer endSalary;

    // 求职状态
    private String jobStatus;

}
