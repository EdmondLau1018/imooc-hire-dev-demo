package com.imooc.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SysParamsVO {

    private Integer id;
    private Integer maxResumeRefreshCounts;

    private Integer version;

}
