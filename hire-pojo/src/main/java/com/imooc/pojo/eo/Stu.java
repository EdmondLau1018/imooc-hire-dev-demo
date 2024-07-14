package com.imooc.pojo.eo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
@Document(indexName = "stu")
public class Stu {

    @Id
    private Long stuId;
    @Field
    private String name;
    @Field
    private Integer age;
    @Field
    private float money;
    @Field
    private String description;
}
