package com.imooc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imooc.pojo.CompanyPhoto;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 企业相册表，本表只存企业上传的图片 Mapper 接口
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
@Repository
public interface CompanyPhotoMapper extends BaseMapper<CompanyPhoto> {

}
