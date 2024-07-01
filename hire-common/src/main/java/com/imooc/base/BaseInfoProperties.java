package com.imooc.base;

import com.github.pagehelper.PageInfo;
import com.imooc.utils.PagedGridResult;
import com.imooc.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BaseInfoProperties {

    @Autowired
    public RedisOperator redis;

    public static final String SYMBOL_DOT = ".";       // 小圆点，无意义，可用可不用

    public static final String TOKEN_USER_PREFIX = "app";       // app端的用户token前缀
    public static final String TOKEN_SAAS_PREFIX = "saas";      // 企业saas平台的用户token前缀
    public static final String TOKEN_ADMIN_PREFIX = "admin";    // 运营管理平台的用户token前缀

    public static final String APP_USER_JSON = "app-user-json";       // app端的用户
    public static final String SAAS_USER_JSON = "saas-user-json";      // 企业saas平台的用户
    public static final String ADMIN_USER_JSON = "admin-user-json";    // 运营管理平台的用户

    public static final Integer COMMON_START_PAGE = 1;
    public static final Integer COMMON_START_PAGE_ZERO = 0;
    public static final Integer COMMON_PAGE_SIZE = 10;

    public static final String MOBILE_SMSCODE = "mobile:smscode";
    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final String REDIS_USER_INFO = "redis_user_info";

    public static final String REDIS_ADMIN_TOKEN = "redis_admin_token";
    public static final String REDIS_ADMIN_INFO = "redis_admin_info";

    public static final String SAAS_PLATFORM_LOGIN_TOKEN = "saas_platform_login_token";
    public static final String SAAS_PLATFORM_LOGIN_TOKEN_READ = "saas_platform_login_token_read";

    public static final String REDIS_SAAS_USER_TOKEN = "redis_saas_user_token";
    public static final String REDIS_SAAS_USER_INFO = "redis_saas_user_info";

    public static final String TOP_INDUSTRY_LIST = "top_industry_list";
    public static final String THIRD_INDUSTRY_LIST = "third_industry_list";

    public static final String DATA_DICTIONARY_LIST_TYPECODE = "data_dictionary_list_typecode";

    // 某个字典code下所对应的所有字典列表
    public static final String REDIS_DATA_DICTIONARY_ITEM_LIST = "redis_data_dictionary_item_list";

    // 企业信息相关
    public static final String REDIS_COMPANY_BASE_INFO = "company_base_info";
    public static final String REDIS_COMPANY_MORE_INFO = "company_more_info";
    public static final String REDIS_COMPANY_PHOTOS = "redis_company_photos";
    public static final String REDIS_COMPANY_IS_VIP = "redis_company_is_vip";
    public static final String REDIS_COMPANY_HR_COUNTS = "redis_company_hr_counts";

    // 用户简历信息
    public static final String REDIS_RESUME_INFO = "redis_resume_info";
    public static final String REDIS_MAX_RESUME_REFRESH_COUNTS = "max_resume_refresh_counts";
    public static final String ZK_MAX_RESUME_REFRESH_COUNTS = "max_resume_refresh_counts";
    public static final String CACHE_MAX_RESUME_REFRESH_COUNTS = "max_resume_refresh_counts";
    public static final String USER_ALREADY_REFRESHED_COUNTS = "user_already_refreshed_counts";
    public static final String REDIS_RESUME_EXPECT = "redis_resume_expect";

    public static final String DELAY_ERROR_RETRY_COUNTS = "delay_error_retry_counts";

    public static final String HR_COLLECT_RESUME_COUNTS = "hr_collect_resume_counts";
    public static final String HR_READ_RESUME_RECORD_COUNTS = "hr_read_resume_record_counts";
    public static final String WHO_LOOK_ME_COUNTS = "who_look_me_counts";
    public static final String CAND_FOLLOW_HR_COUNTS = "cand_follow_hr_counts";
    public static final String CAND_COLLECT_JOB_COUNTS = "cand_collect_job_counts";

    // HR的面试记录数
    public static final String HR_INTERVIEW_RECORD_COUNTS = "hr_interview_record_counts";
    // 候选人的面试记录数
    public static final String CAND_INTERVIEW_RECORD_COUNTS = "cand_interview_record_counts";

    // 职位信息
    public static final String REDIS_JOB_DETAIL = "redis_job_detail";
    public static final String HR_ALL_JOB_COUNTS = "hr_all_job_counts";

    public static final String CHAT_MSG_LIST = "chat_msg_list";

    // 文章阅读总数
    public static final String REDIS_ARTICLE_READ_COUNTS = "redis_article_read_counts";
    // 标记用户阅读，与article关系
    public static final String REDIS_USER_READ_ARTICLE = "redis_user_read_article";

    // 短视频的评论总数
    public static final String REDIS_VLOG_COMMENT_COUNTS = "redis_vlog_comment_counts";
    // 短视频的评论喜欢数量
    public static final String REDIS_VLOG_COMMENT_LIKED_COUNTS = "redis_vlog_comment_liked_counts";
    // 用户点赞评论
    public static final String REDIS_USER_LIKE_COMMENT = "redis_user_like_comment";

    // 我的关注总数
    public static final String REDIS_MY_FOLLOWS_COUNTS = "redis_my_follows_counts";
    // 我的粉丝总数
    public static final String REDIS_MY_FANS_COUNTS = "redis_my_fans_counts";
    // 博主和粉丝的关联关系，用于判断他们是否互粉
    public static final String REDIS_FANS_AND_VLOGGER_RELATIONSHIP = "redis_fans_and_vlogger_relationship";

    // 视频和发布者获赞数
    public static final String REDIS_VLOG_BE_LIKED_COUNTS = "redis_vlog_be_liked_counts";
    public static final String REDIS_VLOGER_BE_LIKED_COUNTS = "redis_vloger_be_liked_counts";

    // 用户是否喜欢/点赞视频，取代数据库的关联关系，1：喜欢，0：不喜欢（默认） redis_user_like_vlog:{userId}:{vlogId}
    public static final String REDIS_USER_LIKE_VLOG = "redis_user_like_vlog";


    // 支付中心地址 - 创建商户订单
//    public static final String PAYMENT_URL_CREATE_MERCHANT_ORDER = "http://192.168.1.6:9060/payment/createMerchantOrder";		// dev
    public static final String PAYMENT_URL_CREATE_MERCHANT_ORDER = "http://172.17.172.127:9060/payment/createMerchantOrder";		// prod
    //    String PAYMENT_URL_CREATE_MERCHANT_ORDER = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";		// produce
    // 支付中心地址 - 获得微信支付二维码
//    public static final String PAYMENT_URL_GET_WXPAY_QRCODE = "http://192.168.1.6:9060/payment/getWXPayQRCode";		// dev
    public static final String PAYMENT_URL_GET_WXPAY_QRCODE = "http://172.17.172.127:9060/payment/getWXPayQRCode";		// prod
//    String PAYMENT_URL_GET_WXPAY_QRCODE = "http://payment.t.mukewang.com/foodie-payment/payment/getWXPayQRCode";		// produce

    // 慕聘网 - 支付后的回调通知api接口地址
//    public static final String PAY_RETURN_URL = "http://192.168.1.6:6001/tradeOrder/notifyMerchantOrderPaid";             // dev
    public static final String PAY_RETURN_URL = "http://172.17.172.127:6001/tradeOrder/notifyMerchantOrderPaid";             // prod
//    public static final String PAY_RETURN_URL = "http://api.t.mukewang.com/foodie-api/tradeOrder/notifyMerchantOrderPaid";        // prod


//    public Map<String, String> getErrors(BindingResult result) {
//        Map<String, String> map = new HashMap<>();
//        List<FieldError> errorList = result.getFieldErrors();
//        for (FieldError ff : errorList) {
//            // 错误所对应的属性字段名
//            String field = ff.getField();
//            // 错误的信息
//            String msg = ff.getDefaultMessage();
//            map.put(field, msg);
//        }
//        return map;
//    }

    public PagedGridResult setterPagedGrid(List<?> list,
                                           Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult gridResult = new PagedGridResult();
        gridResult.setRows(list);
        gridResult.setPage(page);
        gridResult.setRecords(pageList.getTotal());
        gridResult.setTotal(pageList.getPages());
        return gridResult;
    }

}
