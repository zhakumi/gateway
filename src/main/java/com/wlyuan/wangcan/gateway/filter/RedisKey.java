package com.wlyuan.wangcan.gateway.filter;

public enum RedisKey {
  JWT("jwt", "jwt缓存key");
  private String code;
  private String desc;

  private RedisKey(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  /**
   * 获取JWT缓存key
   */
  public static String getTmsUserJwtKey(Long userCompanyid, String roleIds, Long userid) {
    return JWT.code + ":" + userCompanyid.toString() + ":" + roleIds + ":" + userid.toString();
  }

  public String getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }
}
