package com.lframework.xingyun.basedata.vo.paytype;

import com.lframework.starter.web.components.validation.IsCode;
import com.lframework.starter.web.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePayTypeVo implements BaseVo, Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 编号
   */
  @ApiModelProperty(value = "编号", required = true)
  @IsCode
  @NotBlank(message = "请输入编号！")
  private String code;

  /**
   * 名称
   */
  @ApiModelProperty(value = "名称", required = true)
  @NotBlank(message = "请输入名称！")
  private String name;

  /**
   * 是否记录内容
   */
  @ApiModelProperty(value = "是否记录内容", required = true)
  @NotNull(message = "是否记录内容不能为空！")
  private Boolean recText;

  /**
   * 备注
   */
  @ApiModelProperty("备注")
  private String description;
}
