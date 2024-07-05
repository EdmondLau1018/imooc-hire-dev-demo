package com.imooc.mapper;

import com.imooc.pojo.vo.CompanyInfoVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 企业表 Mapper 接口
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
@Repository
public interface CompanyMapperCustom {

    /**
     * 公司审核页面查询公司列表
     * @return
     */
    public List<CompanyInfoVO> queryCompanyList(@Param("paramMap")Map<String,Object> paramMap);

    /**
     * 根据公司 id 查询公司具体信息
     * @param paramMap
     * @return
     */
    public CompanyInfoVO queryCompanyInfo(@Param("paramMap")Map<String,Object> paramMap);
}
