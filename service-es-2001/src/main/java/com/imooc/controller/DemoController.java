package com.imooc.controller;

import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.eo.SearchResumesEO;
import com.imooc.pojo.eo.Stu;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 分页从 ES 全量查询
     * @param page
     * @param pageSize
     * @return
     */
    @PostMapping("/matchAll")
    public GraceJSONResult matchAll(Integer page, Integer pageSize) {

        if (page < 1) page = 1;
        //ES 的分页是从 0 开始的
        page--;

        PageRequest pageable = PageRequest.of(page, pageSize);
        //  多条件查询构造器
        Query query = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery())
                .withPageable(pageable)
                .build();

        SearchHits<SearchResumesEO> hits = esTemplate.search(query, SearchResumesEO.class);
        List<SearchHit<SearchResumesEO>> hitList = hits.getSearchHits();
        // 从 ES 查询结果 hits 中获取 返回结果 list
        ArrayList<SearchResumesEO> list = new ArrayList<>();
        for (SearchHit<SearchResumesEO> searchHit : hitList) {
            SearchResumesEO content = searchHit.getContent();
            list.add(content);
        }
        return GraceJSONResult.ok(list);
    }
}
