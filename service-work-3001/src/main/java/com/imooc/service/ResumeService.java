package com.imooc.service;

import com.imooc.pojo.ResumeEducation;
import com.imooc.pojo.ResumeExpect;
import com.imooc.pojo.ResumeProjectExp;
import com.imooc.pojo.ResumeWorkExp;
import com.imooc.pojo.bo.*;
import com.imooc.pojo.vo.ResumeVO;

import java.util.List;

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
     *
     * @param projectExpId
     * @param userId
     */
    public void deleteProjectExp(String projectExpId, String userId);

    /**
     * 新增或者修改教育经历
     *
     * @param editEducationBO
     */
    public void editEducation(EditEducationBO editEducationBO);

    /**
     * 查询用户对应的教育经历
     *
     * @param eduId
     * @param userId
     * @return
     */
    public ResumeEducation getEducation(String eduId, String userId);

    /**
     * 删除教育经历
     *
     * @param eduId
     * @param userId
     */
    public void deleteEducation(String eduId, String userId);

    /**
     * 编辑求职期望
     *
     * @param editResumeExpectBO
     */
    public void editResumeExpect(EditResumeExpectBO editResumeExpectBO);

    /**
     * 查询我的期望列表
     *
     * @param resumeId
     * @param userId
     * @return
     */
    public List<ResumeExpect> getMyResumeExpect(String resumeId, String userId);

    /**
     * 根据 id 删除求职期望信息
     * @param resumeId
     * @param userId
     */
    public void deleteMyResumeExpect(String resumeId, String userId);

    /**
     *  简历刷新业务接口
     * @param userId
     * @param resumeId
     */
    public void refreshResume(String userId,String resumeId);
}
