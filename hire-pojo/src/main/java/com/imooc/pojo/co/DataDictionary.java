package com.imooc.pojo.co;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DataDictionary {

    @Id
    private String id;
    private String type_code;
    private String type_name;
    private String item_key;
    private String item_value;
    private Integer sort;
    private String icon;
    private Boolean enable;
}
