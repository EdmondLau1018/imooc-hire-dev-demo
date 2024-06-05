package com.imooc.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Desc: 学历 枚举
 */
public enum EduEnum {
    JUNIOR("初中",1),
    TECHNICAL("中专/技校", 2),
    HIGH("高中", 3),
    JUNIOR_COLLEGE("大专", 4),
    UNDER_GRADUATE("本科", 5),
    MASTER("硕士", 6),
    MBA("MBA/EMBA", 7),
    DOCTOR("博士", 8);

    public final String edu;
    public final Integer index;

    EduEnum(String edu, Integer index) {
        this.edu = edu;
        this.index = index;
    }

    // 根据index去获得edu的列表，比如index=5，为本科，那么高于本科学历的都可以作为查询条件，因为博士本身就必定高于本科
    public static List getEduList(Integer index) {
        if (index == 0) {
            return null;
        }

        List<String> eduList = new ArrayList<>();

        EduEnum[] eduEnum = EduEnum.values();
        for (EduEnum e : eduEnum) {
            if (e.index >= index) {
                eduList.add(e.edu);
            }
        }

        return eduList;
    }

    public static Integer getEduIndex(String eduStr) {
        if (StringUtils.isBlank(eduStr)) {
            return 0;
        }

        EduEnum[] eduEnum = EduEnum.values();
        for (EduEnum e : eduEnum) {
            if (e.edu.equalsIgnoreCase(eduStr)) {
                return e.index;
            }
        }
        // 如果没有匹配的，则使用默认的0，0为没有，sql查询不作为条件
        return 0;
    }
}
