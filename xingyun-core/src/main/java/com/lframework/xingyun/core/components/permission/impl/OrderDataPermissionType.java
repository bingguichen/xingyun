package com.lframework.xingyun.core.components.permission.impl;

import com.lframework.starter.mybatis.components.permission.SysDataPermissionDataPermissionType;
import org.springframework.stereotype.Component;

@Component
public class OrderDataPermissionType implements SysDataPermissionDataPermissionType {

  @Override
  public Integer getCode() {
    return 2;
  }
}
