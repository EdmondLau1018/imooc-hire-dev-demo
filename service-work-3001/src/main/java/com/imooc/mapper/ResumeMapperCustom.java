package com.imooc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imooc.pojo.Resume;
import com.imooc.pojo.vo.SearchResumesVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 简历表 Mapper 接口
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
@Repository
public interface ResumeMapperCustom extends BaseMapper<Resume> {

    /**
     * 搜索简历信息
     * @param map
     * @return
     */
    public List<SearchResumesVO> searchResumesList(@Param("paramMap") Map<String, Object> map);
}
