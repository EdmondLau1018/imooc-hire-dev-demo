package com.imooc.controller;

import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.eo.SearchResumesEO;
import com.imooc.pojo.eo.Stu;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.web.bind.annotation.GetMapping;
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
        esTemplate.indexOps(SearchResumesEO.class).create();
        return GraceJSONResult.ok();
    }

    @PostMapping("/deleteIndex")
    public GraceJSONResult deleteIndex() {
        esTemplate.indexOps(Stu.class).delete();
        return GraceJSONResult.ok();
    }

    @PostMapping("/save")
    public GraceJSONResult saveIndex() {
        Stu stu = new Stu();
        stu.setStuId(1001L);
        stu.setName("imooc");
        stu.setAge(21);
        stu.setDescription("慕课网的学生");
        stu.setMoney(100.2f);

        //  构建 indexQuery 对象
        IndexQuery iq = new IndexQueryBuilder().withObject(stu).build();

        //  创建 indexCoordinate 对象
        IndexCoordinates ic = IndexCoordinates.of("stu");

        esTemplate.index(iq, ic);
        return GraceJSONResult.ok();
    }
}
