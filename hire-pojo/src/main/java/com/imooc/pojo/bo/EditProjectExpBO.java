package com.imooc.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EditProjectExpBO {

    private String id;
    private String userId;
    private String resumeId;
    private String projectName;
    private String roleName;
    private String beginDate;
    private String endDate;
    private String content;
    private String contentHtml;

}
