package com.imooc.controller;

import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.eo.Stu;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/es")
public class DemoController {

    private final ElasticsearchRestTemplate esTemplate;

    public DemoController(ElasticsearchRestTemplate esTemplate) {
        this.esTemplate = esTemplate;
    }

    /**
     * 测试创建索引信息
     *
     * @return
     */
    @PostMapping("/createIndex")
    public GraceJSONResult createIndex() {
        esTemplate.indexOps(Stu.class).create();
        return GraceJSONResult.ok();
    }

    @PostMapping("/deleteIndex")
    public GraceJSONResult deleteIndex() {
        esTemplate.indexOps(Stu.class).delete();
        return GraceJSONResult.ok();
    }
}
