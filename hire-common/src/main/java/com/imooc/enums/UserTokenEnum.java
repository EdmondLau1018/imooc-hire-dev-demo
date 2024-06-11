package com.imooc.enums;

public enum UserTokenEnum {

    APP("app", "app-user-json"),
    SAAS("saas", "saas-user-json"),
    ADMIN("admin", "admin-user-json");

    public final String prefix;

    public final String userJson;

    UserTokenEnum(String prefix, String userJson) {
        this.prefix = prefix;
        this.userJson = userJson;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getUserJson() {
        return userJson;
    }
}
