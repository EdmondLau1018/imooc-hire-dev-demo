package com.imooc.service;

import com.imooc.pojo.ResumeProjectExp;
import com.imooc.pojo.ResumeWorkExp;
import com.imooc.pojo.bo.EditProjectExpBO;
import com.imooc.pojo.bo.EditResumeBO;
import com.imooc.pojo.bo.EditWorkExpBO;
import com.imooc.pojo.vo.ResumeVO;

/**
 * <p>
 * 简历表 服务类
 * </p>
 *
 * @author Sharn
 * @since 2024-06-20
 */
public interface ResumeService {

    /**
     * 根据 userid 初始化用户简历
     *
     * @param userId
     */
    public void initResume(String userId, String msgId);

    /**
     * 修改用户简历服务接口
     *
     * @param editResumeBO
     */
    public void modifyResume(EditResumeBO editResumeBO);

    /**
     * 查询当前用户的 简历信息
     *
     * @param userId
     * @return
     */
    public ResumeVO getResumeInfo(String userId);

    /**
     * 修改工作经验  业务接口
     *
     * @param editWorkExpBO
     */
    public void editWorkExp(EditWorkExpBO editWorkExpBO);

    /**
     * 根据工作经验 id 和用户 id 查询单个用户的工作经验列表
     *
     * @param workExpId
     * @param userId
     * @return
     */
    public ResumeWorkExp getWorkExp(String workExpId, String userId);

    /**
     * 删除工作经验详情内容
     *
     * @param workExpId
     * @param userId
     */
    public void deleteWorkExp(String workExpId, String userId);

    /**
     * 新增 编辑 项目经验业务接口
     *
     * @param editProjectExpBO
     */
    public void editProjectExp(EditProjectExpBO editProjectExpBO);

    /**
     * 查询项目经验 详情业务接口
     *
     * @param projectExpId
     * @param userId
     * @return
     */
    public ResumeProjectExp getProjectExp(String projectExpId, String userId);

    /**
     * 删除项目经验
     * @param projectExpId
     * @param userId
     */
    public void deleteProjectExp(String projectExpId, String userId);
}
