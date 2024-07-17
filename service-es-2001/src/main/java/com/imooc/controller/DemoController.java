package com.imooc.controller;

import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.eo.SearchResumesEO;
import com.imooc.pojo.eo.Stu;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
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
        //  使用 indexOps 创建和删除相关的索引信息
        esTemplate.indexOps(SearchResumesEO.class).create();
        return GraceJSONResult.ok();
    }

    /**
     * 测试删除索引信息
     *
     * @return
     */
    @PostMapping("/deleteIndex")
    public GraceJSONResult deleteIndex() {
        esTemplate.indexOps(Stu.class).delete();
        return GraceJSONResult.ok();
    }

    /**
     * ElasticSearch 新增文档
     * 使用 index 通过 indexQuery 参数进行新增
     *
     * @return
     */
    @PostMapping("/save")
    public GraceJSONResult saveDocument() {

        //  构建实体类
        Stu stu = new Stu();
        stu.setStuId(1001L);
        stu.setName("imooc");
        stu.setAge(21);
        stu.setDescription("慕课网的学生");
        stu.setMoney(100.2f);

        //  构建 indexQuery 对象 // 将实体类对象添加到 IQ 参数中 构建新增参数内容
        IndexQuery iq = new IndexQueryBuilder().withObject(stu).build();

        //  创建 indexCoordinate 对象 IndexCoordinate ：标注当前操作对应 ES 中使用的索引
        IndexCoordinates ic = IndexCoordinates.of("stu");

        esTemplate.index(iq, ic);
        return GraceJSONResult.ok();
    }

    /**
     * 更新（修改）文档内容
     *
     * @return
     */
    @PostMapping("/update")
    public GraceJSONResult updateDocument() {

        //  构建修改内容 map
        HashMap<String, Object> map = new HashMap<>();
        map.put("age", 25);
        map.put("money", 10000000f);
        map.put("description", "修改对象信息");

        //  构建携带修改map信息的 UpdateQuery 对象
        UpdateQuery updateQuery = UpdateQuery.builder("1001")
                .withDocument(Document.from(map))   //  携带修改后的 对象信息 map
                .build();

        //  执行 更新语句 通过 IndexCoordinate.of 指明需要修改的 索引名称
        esTemplate.update(updateQuery, IndexCoordinates.of("stu"));
        return GraceJSONResult.ok();
    }

    /**
     * 单个文档对象 条件查询
     *
     * @return
     */
    @PostMapping("/get")
    public GraceJSONResult get() {

        //  构建条件对象
        Criteria criteria = new Criteria().and("stuId").is("1001");
        //  构建 条件查询 CriteriaQuery 对象
        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);
        //  使用 searchOne 查询单个对象
        Stu stu = esTemplate.searchOne(criteriaQuery, Stu.class).getContent();

        return GraceJSONResult.ok(stu);
    }

    /**
     * 删除单个文档对象
     * @param id
     * @return
     */
    @PostMapping("/delete")
    public GraceJSONResult deleteDocument(String id) {

        //  通过 id 删除文档对象
        String stu = esTemplate.delete(id, IndexCoordinates.of("stu"));
        return GraceJSONResult.ok(stu);
    }

    /**
     * 分页从 ES 全量查询
     *
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

    /**
     * 带有条件的 ES 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @PostMapping("/term")
    public GraceJSONResult term(Integer page, Integer pageSize) {

        if (page < 1) page = 1;
        page--;

        PageRequest pageable = PageRequest.of(page, pageSize);

        // 传概念条件查询构造器
        Query query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("sex", 1))
                .withQuery(QueryBuilders.matchQuery("major", "信息"))
                .withPageable(pageable)
                .build();

        //  获取查询结果
        SearchHits<SearchResumesEO> searchHits = esTemplate.search(query, SearchResumesEO.class);
        List<SearchResumesEO> list = getESSearchHitsList(searchHits, SearchResumesEO.class);

        return GraceJSONResult.ok(list);
    }

    /**
     * 解析 ES 查询出的 结果 返回对应的 列表 List
     *
     * @param searchHits
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> List<T> getESSearchHitsList(SearchHits<T> searchHits, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        for (SearchHit<T> searchHit : searchHits) {
            T content = searchHit.getContent();
            list.add(content);
        }
        return list;
    }
}
