package com.imooc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.imooc.base.BaseInfoProperties;
import com.imooc.enums.CompanyReviewStatus;
import com.imooc.enums.YesOrNo;
import com.imooc.exceptions.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.mapper.CompanyMapper;
import com.imooc.mapper.CompanyMapperCustom;
import com.imooc.mapper.CompanyPhotoMapper;
import com.imooc.pojo.Company;
import com.imooc.pojo.CompanyPhoto;
import com.imooc.pojo.bo.CreateCompanyBO;
import com.imooc.pojo.bo.ModifyCompanyInfoBO;
import com.imooc.pojo.bo.QueryCompanyBO;
import com.imooc.pojo.bo.ReviewCompanyBO;
import com.imooc.pojo.vo.CompanyInfoVO;
import com.imooc.service.CompanyService;
import com.imooc.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 企业表 服务实现类
 * </p>
 *
 * @author Sharn
 * @since 2024-07-01
 */
@Service
public class CompanyServiceImpl extends BaseInfoProperties implements CompanyService {

    private final CompanyMapper companyMapper;

    private final CompanyMapperCustom companyMapperCustom;

    private final CompanyPhotoMapper companyPhotoMapper;

    public CompanyServiceImpl(CompanyMapper companyMapper, CompanyMapperCustom companyMapperCustom, CompanyPhotoMapper companyPhotoMapper) {
        this.companyMapper = companyMapper;
        this.companyMapperCustom = companyMapperCustom;
        this.companyPhotoMapper = companyPhotoMapper;
    }

    @Override
    public Company getByFullName(String fullName) {

        Company company = companyMapper.selectOne(
                new QueryWrapper<Company>()
                        .eq("company_name", fullName)
        );
        return company;
    }

    /**
     * 创建新的公司信息 提交审核 实现方法
     *
     * @param createCompanyBO
     * @return
     */
    @Transactional
    @Override
    public String createNewCompany(CreateCompanyBO createCompanyBO) {

        Company newCompany = new Company();
        BeanUtils.copyProperties(createCompanyBO, newCompany);

        newCompany.setIsVip(YesOrNo.NO.type);
        //   将公司的审核状态属性设置为  未审核
        newCompany.setReviewStatus(CompanyReviewStatus.NOTHING.type);
        //  设置时间属性
        newCompany.setCreatedTime(LocalDateTime.now());
        newCompany.setUpdatedTime(LocalDateTime.now());

        companyMapper.insert(newCompany);
        return newCompany.getId();
    }

    /**
     * 更新公司信息 创建的公司信息审核未通过
     *
     * @param createCompanyBO
     * @return
     */
    @Transactional
    @Override
    public String resetCompanyReview(CreateCompanyBO createCompanyBO) {

        Company newCompany = new Company();
        BeanUtils.copyProperties(createCompanyBO, newCompany);

        newCompany.setId(createCompanyBO.getCompanyId());
        newCompany.setIsVip(YesOrNo.NO.type);
        newCompany.setReviewStatus(CompanyReviewStatus.NOTHING.type);
        newCompany.setUpdatedTime(LocalDateTime.now());

        companyMapper.updateById(newCompany);

        return createCompanyBO.getCompanyId();
    }

    /**
     * 根据企业 id 查询 企业信息 实现方法
     *
     * @param companyId
     * @return
     */
    @Override
    public Company getById(String companyId) {
        return companyMapper.selectById(companyId);
    }

    /**
     * 更新待审核的公司信息实现方法
     *
     * @param reviewCompanyBO
     */
    @Transactional
    @Override
    public void commitReviewCompanyInfo(ReviewCompanyBO reviewCompanyBO) {

        Company pendingCompany = new Company();
        pendingCompany.setId(reviewCompanyBO.getCompanyId());
        pendingCompany.setReviewStatus(reviewCompanyBO.getReviewStatus());
        //  如果上次的审核信息未通过 （重置审核信息）
        pendingCompany.setReviewReplay("");
        pendingCompany.setAuthLetter(reviewCompanyBO.getAuthLetter());

        pendingCompany.setCommitUserId(reviewCompanyBO.getHrUserId());
        pendingCompany.setCommitUserMobile(reviewCompanyBO.getHrMobile());
        pendingCompany.setCommitDate(LocalDate.now());

        pendingCompany.setUpdatedTime(LocalDateTime.now());

        companyMapper.updateById(pendingCompany);
    }

    @Override
    public PagedGridResult queryCompanyListPaged(QueryCompanyBO queryCompanyBO, Integer page, Integer pageSize) {

        PageHelper.startPage(page, pageSize);

        //  构建查询参数
        HashMap<String, Object> map = new HashMap<>();
        map.put("companyName", queryCompanyBO.getCompanyName());
        map.put("commitUser", queryCompanyBO.getCommitUser());
        map.put("reviewStatus", queryCompanyBO.getReviewStatus());
        map.put("commitDateStart", queryCompanyBO.getCommitDateStart());
        map.put("commitDateEnd", queryCompanyBO.getCommitDateEnd());

        List<CompanyInfoVO> companyList = companyMapperCustom.queryCompanyList(map);

        return setterPagedGrid(companyList, page);
    }

    /**
     * 根据公司 id 查询企业基本信息
     *
     * @param companyId
     * @return
     */
    @Override
    public CompanyInfoVO queryCompanyInfo(String companyId) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("companyId", companyId);

        //  持久层查询公司信息
        CompanyInfoVO companyInfo = companyMapperCustom.queryCompanyInfo(map);
        return companyInfo;
    }

    /**
     * 更新审核后的企业信息
     * 更新的字段是审核的状态和审核信息
     *
     * @param reviewCompanyBO
     */
    @Transactional
    @Override
    public void updateReviewInfo(ReviewCompanyBO reviewCompanyBO) {

        Company pendingCompany = new Company();
        pendingCompany.setId(reviewCompanyBO.getCompanyId());
        pendingCompany.setReviewStatus(reviewCompanyBO.getReviewStatus());
        pendingCompany.setReviewReplay(reviewCompanyBO.getReviewReplay());
        pendingCompany.setUpdatedTime(LocalDateTime.now());

        companyMapper.updateById(pendingCompany);
    }

    /**
     * app 端修改企业信息接口实现
     * 新增技术点：redis 分布式锁
     *
     * @param modifyCompanyInfoBO
     */
    @Transactional
    @Override
    public void modifyCompanyInfo(ModifyCompanyInfoBO modifyCompanyInfoBO) throws InterruptedException {

        String distLockName = "redis_lock";
        String selfLockId = UUID.randomUUID().toString();

        //  循环设置 redis 分布式锁
        while (redis.setnx(distLockName, selfLockId, 30)) {
            Thread.sleep(2000);
        }
        try {
            //  当前线程加锁成功，执行业务
            doModify(modifyCompanyInfoBO);
        } finally {
            //  判断是不是加了锁的线程 如果是 那就释放锁
//            if (redis.get(selfLockId).equalsIgnoreCase(selfLockId)) {
//                redis.del(distLockName);
//            }
            //  用 LUA 脚本 删除分布式锁，先获取redis 中对应的key
            //  判断与 方法传递的 key 是否一致 ，如果一致那就删除这个分布式锁
            String lockScript =
                    " if redis.call('get',KEYS[1]) == ARGV[1] "
                            + " then "
                            + " return redis.call('del',KEYS[1]) "
                            + " else "
                            + " return 0 "
                            + " end ";
            redis.execLuaScript(lockScript, selfLockId, distLockName);
        }
    }

    /**
     * redis 分布式锁 优化前的代码
     *
     * @param modifyCompanyInfoBO
     * @throws InterruptedException
     */
    @Transactional
    public void modifyCompanyInfo2(ModifyCompanyInfoBO modifyCompanyInfoBO) throws InterruptedException {

        //  1. 获得锁，值随意只要不为空即可
        String distLockName = "redis_lock";
        //  给对应的 设置锁的线程 设置 唯一 id 确保只有设置锁的线程才可以释放锁
        String selfLockId = UUID.randomUUID().toString();
        //  在设置 redis 分布式锁的时候新增 过期时间
        Boolean isLockOK = redis.setnx(distLockName, selfLockId, 30);
        //  2. 判断是否加锁成功（当前线程是否获得锁）
        if (isLockOK) {
            //  3. 执行更新业务流程
            doModify(modifyCompanyInfoBO);
            //  4. 执行业务结束，释放锁
            //  4-1. 判断这个锁是不是当前线程设置的如果是 才删除锁
            if (redis.get(distLockName).equalsIgnoreCase(selfLockId) && redis.get(distLockName) != null) {
                redis.del(distLockName);
            }
        } else {
            //  3-1.    加锁失败 ，重试当前方法
            //      不要立即重试 ，需要等待一段时间重试
            Thread.sleep(100);
            modifyCompanyInfo(modifyCompanyInfoBO);
        }

        doModify(modifyCompanyInfoBO);
    }

    /**
     * 更新 企业信息 业务实现
     *
     * @param modifyCompanyInfoBO
     */
    public void doModify(ModifyCompanyInfoBO modifyCompanyInfoBO) {

        //  校验 企业 id (主键是否为空 )
        String companyId = modifyCompanyInfoBO.getCompanyId();
        if (StringUtils.isBlank(companyId))
            GraceException.displayException(ResponseStatusEnum.COMPANY_INFO_UPDATED_ERROR);

        //  对象信息拷贝
        Company pendingCompany = new Company();
        pendingCompany.setId(modifyCompanyInfoBO.getCompanyId());
        pendingCompany.setUpdatedTime(LocalDateTime.now());

        BeanUtils.copyProperties(modifyCompanyInfoBO, pendingCompany);

        //  持久层更新对象信息
        companyMapper.updateById(pendingCompany);

        //  修改企业信息之后删除缓存
        redis.del(REDIS_COMPANY_BASE_INFO + ":" + companyId);
        redis.del(REDIS_COMPANY_MORE_INFO + ":" + companyId);
    }

    /**
     * 保存 企业相册实现方法
     *
     * @param companyInfoBO
     */
    @Transactional
    @Override
    public void savePhotos(ModifyCompanyInfoBO companyInfoBO) {

        CompanyPhoto companyPhoto = new CompanyPhoto();
        companyPhoto.setCompanyId(companyInfoBO.getCompanyId());
        companyPhoto.setPhotos(companyInfoBO.getPhotos());

        //  获取当前企业是否存在相册记录，如果存在就修改 ，不存在则新增
        CompanyPhoto photos = getPhotos(companyInfoBO.getCompanyId());
        if (photos == null) {
            //  在 企业相册表中不存在相关记录 ，新增流程
            companyPhotoMapper.insert(companyPhoto);
        } else {
            //  存在当前企业的 相册记录 执行修改流程
            companyPhotoMapper.update(companyPhoto,
                    new UpdateWrapper<CompanyPhoto>()
                            .eq("company_id", companyInfoBO.getCompanyId())
            );
        }
    }

    /**
     * 根据 企业 id 查询 企业相册表
     * 当前企业是否存在 相册记录
     *
     * @param companyId
     * @return
     */
    public CompanyPhoto getPhotos(String companyId) {

        //  根据 企业id 查询企业相册是否存在记录
        CompanyPhoto companyPhoto = companyPhotoMapper.selectOne(new QueryWrapper<CompanyPhoto>()
                .eq("company_id", companyId));

        return companyPhoto;
    }
}
