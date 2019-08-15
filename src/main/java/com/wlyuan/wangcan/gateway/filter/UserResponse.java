package com.wlyuan.wangcan.gateway.filter;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserResponse implements Serializable {
    public Long userId;
    public String userName;
    public Long userCompanyid;
    public String userCompanyName;
    public String roleIds;
    public String userPhone;
}
