package com.imooc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.mapper.StuMapper;
import com.imooc.pojo.Stu;
import com.imooc.service.StuService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Sharn
 * @since 2024-05-27
 */
@Service
public class StuServiceImpl implements StuService {

    private final StuMapper stuMapper;

    public StuServiceImpl(StuMapper stuMapper) {
        this.stuMapper = stuMapper;
    }

    @Override
    public Integer save(Stu stu) {
        return stuMapper.insert(stu);
    }
}
