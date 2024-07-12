SELECT
    # 用户基本信息
    u.id as userId,
    r.id as resumeId,
    u.nickname as nickname,
    u.sex as sex,
    u.face as face,

    # 年龄，需计算
    u.birthday as birthday,
    now() as now,
    to_days(u.birthday) as birthdayDays,
    to_days(now()) as nowDays,
    FLOOR( (to_days(now()) - to_days(u.birthday))/365 ) as age,

    # 最新工作单位
    latest_company.companyName as companyName,
    latest_company.position as position,
    latest_company.industry as industry,

    # 最高学历
    highest_edu.school as school,
    highest_edu.education as education,
    highest_edu.major as major,

    # 工作年限
    u.start_work_date as startWorkDate,
    to_days(u.start_work_date) as startWorkDateDays,
    FLOOR( (to_days(now()) - to_days(u.start_work_date))/365 ) as workYears,

    re.id as resumeExpectId,
    # 职位类别
    re.job_name as jobType,
    re.city as city,

    # 期望薪资
    re.begin_salary as beginSalary,
    re.end_salary as endSalary,

    # 技能标签
    r.skills as skills,
    # 个人优势简介
    r.advantage as advantage,
    r.advantage_html as advantageHtml,
    # 资格证书
    r.credentials as credentials,
    # 简历状态
    r.`status` as jobStatus,
    # 简历刷新日期
    r.refresh_time as refreshTime,
    TIMESTAMPDIFF(SECOND, r.refresh_time, NOW()) as activeTimeSeconds
FROM
    resume r
        LEFT JOIN
    users u
    on
            r.user_id = u.id
        LEFT JOIN
    resume_expect re
    on
            r.id = re.resume_id

        LEFT JOIN
    (SELECT
         ANY_VALUE(edu.id) as eduId,
         edu.user_id as userId,
         ANY_VALUE(edu.resume_id) as resumeId,
         ANY_VALUE(edu.education) as education,
         ANY_VALUE(edu.school) as school,
         ANY_VALUE(edu.major) as major,
         ANY_VALUE(edu.begin_date) as beginDate,
         ANY_VALUE(edu.end_date) as endDate,
         max(edu.end_date) as max_day
     from
         (select
              *,
              max(end_date) over(partition by user_id ORDER BY end_date desc) as max_day
          from
              resume_education) edu
     group by
         edu.user_id) as highest_edu
    on
            highest_edu.userId = r.user_id

        LEFT JOIN
    (select
         workExp.user_id as userId,
         ANY_VALUE(workExp.resume_id) as resumeId,
         ANY_VALUE(workExp.company_name) as companyName,
         ANY_VALUE(workExp.position) as position,
         ANY_VALUE(workExp.industry) as industry,
         ANY_VALUE(workExp.begin_date) as beginDate,
         ANY_VALUE(workExp.end_date) as endDate,
         max(workExp.end_date) as max_day
     from
         (select
              *,
              max(end_date) over(partition by user_id ORDER BY end_date desc) as max_day
          from
              resume_work_exp) workExp
     group by
         workExp.user_id) as latest_company
    ON
            latest_company.userId = r.user_id
WHERE
        1=1
  # 求职期望不能为空，没有求职期望的简历不能被查询到
  AND re.id is not null
  # 判断工作年限
--     AND FLOOR( (to_days(now()) - to_days(u.start_work_date))/365 ) >= 1
  # 判断basicTitle，模糊搜索(匹配)简历的 姓名、个人优势、资格证书、技能标签
--     AND (u.nickname LIKE '%风间%' OR r.advantage LIKE '%风间%' OR r.credentials LIKE '%风间%' OR r.skills LIKE '%风间%' OR re.job_name LIKE '%风间%')
  # 判断jobType
--     AND re.job_name LIKE '%工程师%'
  # 判断年龄
--     AND FLOOR( (to_days(now()) - to_days(u.birthday))/365 ) >= 6
--     AND FLOOR( (to_days(now()) - to_days(u.birthday))/365 ) <= 130
  # 判断性别
--     AND u.sex = 2
  # 判断活跃度 举例5分钟（5*60） 举例7天（7 * 24 * 60 * 60）
--     AND TIMESTAMPDIFF(SECOND, r.refresh_time, NOW()) <= 7 * 24 * 60 * 60
  # 判断工作经验（年限）
--     AND FLOOR( (to_days(now()) - to_days(u.start_work_date))/365 ) >= 2
--     AND FLOOR( (to_days(now()) - to_days(u.start_work_date))/365 ) <= 5
  # 判断学历，用最高学历去匹配，最高学历符合in多个条件就行
--     AND highest_edu.education in ('本科', '硕士', '博士')
  # 判断符合的薪资区间
--     AND
--         (
--             re.begin_salary <= 15 AND re.end_salary >= 15
--             OR
--             re.begin_salary <= 20 AND re.end_salary >= 20
--         )
  # 判断求职状态
--     AND r.`status` LIKE '%在职，暂无跳槽打算%'
ORDER BY r.refresh_time desc
;


# 新，开窗函数（窗口函数/分析函数），支持分组多列显示
select
    ANY_VALUE(edu.id),
    edu.user_id as userId,
    ANY_VALUE(edu.resume_id) as resumeId,
    ANY_VALUE(edu.education) as education,
    ANY_VALUE(edu.school) as school,
    ANY_VALUE(edu.major) as major,
    ANY_VALUE(edu.begin_date) as beginDate,
    ANY_VALUE(edu.end_date) as endDate,
    max(edu.end_date) as max_day
from
    (select
         *,
         max(end_date) over(partition by user_id ORDER BY end_date desc) as max_day
     from
         resume_education) edu
group by
    edu.user_id;



select
    workExp.user_id as userId,
    ANY_VALUE(workExp.resume_id) as resumeId,
    ANY_VALUE(workExp.company_name) as companyName,
    ANY_VALUE(workExp.position) as position,
    ANY_VALUE(workExp.industry) as industry,
    ANY_VALUE(workExp.begin_date) as beginDate,
    ANY_VALUE(workExp.end_date) as endDate,
    max(workExp.end_date) as max_day
from
    (select
         *,
         max(end_date) over(partition by user_id ORDER BY end_date desc) as max_day
     from
         resume_work_exp) workExp
group by
    workExp.user_id;
