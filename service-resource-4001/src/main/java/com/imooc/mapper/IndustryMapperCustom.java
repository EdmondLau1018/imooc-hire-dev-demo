package com.imooc.mapper;

import com.imooc.pojo.Industry;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 行业表  自定义 Mapper 接口
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
@Repository
public interface IndustryMapperCustom {

    public List<Industry> getThirdListByTop(@Param("paramMap")Map<String,Object> map);

}
