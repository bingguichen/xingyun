package com.dstz.adaptor.org.xingyun.dto;

import com.dstz.org.api.constant.GroupTypeConstant;
import com.dstz.org.api.model.IGroup;

public class RoleDTO implements IGroup {

  /**
   * 角色ID
   */
  private String groupId;

  /**
   * 角色名称
   */
  private String groupName;

  /**
   * 角色编号
   */
  private String groupCode;

  /**
   * 类型
   */
  private String groupType = GroupTypeConstant.ROLE.key();

  /**
   * 父级ID
   */
  private String parentId;

  @Override
  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  @Override
  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  @Override
  public String getGroupCode() {
    return groupCode;
  }

  public void setGroupCode(String groupCode) {
    this.groupCode = groupCode;
  }

  @Override
  public String getGroupType() {
    return groupType;
  }

  public void setGroupType(String groupType) {
    this.groupType = groupType;
  }

  @Override
  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }
}
