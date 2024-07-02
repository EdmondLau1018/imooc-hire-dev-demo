package com.imooc.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.checkerframework.common.value.qual.ArrayLen;

import java.io.Serializable;

/**
 * <p>
 * 数据字典表
 * </p>
 *
 * @author Sharn
 * @since 2024-05-27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DataDictionaryBO {

    private String id;
    private String typeCode;
    private String typeName;
    private String itemKey;
    private String itemValue;
    private Integer sort;
    private String icon;
    private Boolean enable;

}
