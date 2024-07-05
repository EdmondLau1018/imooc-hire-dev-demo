package com.imooc.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class QueryDictItemsBO {
    private String advantage[];
    private String benefits[];
    private String bonus[];
    private String subsidy[];
}
