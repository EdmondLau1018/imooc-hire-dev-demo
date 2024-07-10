package com.imooc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imooc.pojo.Job;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * HR发布的职位表 Mapper 接口
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
@Repository
public interface JobMapper extends BaseMapper<Job> {

}
