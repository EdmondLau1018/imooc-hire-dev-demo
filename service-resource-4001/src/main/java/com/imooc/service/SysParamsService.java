package com.imooc.service;

import com.imooc.pojo.SysParams;

/**
 * <p>
 * 系统参数配置表，本表仅有一条记录 服务类
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
public interface SysParamsService  {

    /**
     * 更新支持简历刷新的最大数量
     * @param maxCounts
     * @param version
     */
    public void updateMaxResumeCounts(Integer maxCounts,Integer version);

    /**
     * 获得系统参数
     * @return
     */
    public SysParams getSysParams();
}
