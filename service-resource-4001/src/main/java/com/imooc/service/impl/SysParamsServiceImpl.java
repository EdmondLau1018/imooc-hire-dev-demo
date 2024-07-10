package com.imooc.service.impl;

import com.imooc.base.BaseInfoProperties;
import com.imooc.mapper.SysParamsMapper;
import com.imooc.pojo.SysParams;
import com.imooc.service.SysParamsService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统参数配置表，本表仅有一条记录 服务实现类
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
@Service
public class SysParamsServiceImpl extends BaseInfoProperties implements SysParamsService {

    private final SysParamsMapper sysParamsMapper;

    public SysParamsServiceImpl(SysParamsMapper sysParamsMapper) {
        this.sysParamsMapper = sysParamsMapper;
    }

    /**
     * 更新简历支持刷新的最大数量
     * @param maxCounts
     * @param version
     */
    @Override
    public void updateMaxResumeCounts(Integer maxCounts, Integer version) {

        SysParams sysParams = new SysParams();
        sysParams.setId(1001);
        sysParams.setMaxResumeRefreshCounts(maxCounts);

        sysParamsMapper.updateById(sysParams);
    }

    @Override
    public SysParams getSysParams() {
        return sysParamsMapper.selectById(1001);
    }
}
